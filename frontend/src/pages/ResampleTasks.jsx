import React, { useState, useEffect } from 'react'
import {
  Card,
  Table,
  Tag,
  Button,
  Select,
  Row,
  Col,
  Statistic,
  message,
  Modal,
  Descriptions,
  Space
} from 'antd'
import {
  ReloadOutlined,
  ThunderboltOutlined,
  DropboxOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  EyeOutlined,
  ClockCircleOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { resampleTaskApi, meterApi, enterpriseApi } from '../api'

const ResampleTasks = () => {
  const [tasks, setTasks] = useState([])
  const [meters, setMeters] = useState([])
  const [enterprises, setEnterprises] = useState([])
  const [loading, setLoading] = useState(false)
  const [selectedStatus, setSelectedStatus] = useState(null)
  const [detailModalVisible, setDetailModalVisible] = useState(false)
  const [selectedTask, setSelectedTask] = useState(null)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [tasksRes, metersRes, enterprisesRes] = await Promise.all([
        resampleTaskApi.getAll(),
        meterApi.getAll(),
        enterpriseApi.getAll()
      ])

      setTasks(tasksRes.data)
      setMeters(metersRes.data)
      setEnterprises(enterprisesRes.data)
    } catch (error) {
      console.error('Failed to fetch data:', error)
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const getMeterName = (meterId) => {
    const meter = meters.find(m => m.id === meterId)
    if (meter) {
      return meter.name
    }
    return meterId
  }

  const getMeterType = (meterId) => {
    const meter = meters.find(m => m.id === meterId)
    if (meter) {
      return meter.type
    }
    return null
  }

  const getMeterEnterpriseName = (meterId) => {
    const meter = meters.find(m => m.id === meterId)
    if (meter) {
      const enterprise = enterprises.find(e => e.id === meter.enterpriseId)
      return enterprise ? enterprise.name : meter.enterpriseId
    }
    return '-'
  }

  const getStatusColor = (status) => {
    const colorMap = {
      PENDING: 'orange',
      IN_PROGRESS: 'processing',
      COMPLETED: 'green',
      CANCELLED: 'default',
      FAILED: 'red'
    }
    return colorMap[status] || 'default'
  }

  const getStatusText = (status) => {
    const textMap = {
      PENDING: '待处理',
      IN_PROGRESS: '处理中',
      COMPLETED: '已完成',
      CANCELLED: '已取消',
      FAILED: '失败'
    }
    return textMap[status] || status
  }

  const completeTask = async (taskId) => {
    try {
      await resampleTaskApi.complete(taskId)
      message.success('补采任务已完成')
      fetchData()
    } catch (error) {
      console.error('Failed to complete task:', error)
      message.error('完成失败')
    }
  }

  const failTask = async (taskId) => {
    try {
      await resampleTaskApi.fail(taskId)
      message.success('补采任务已标记为失败')
      fetchData()
    } catch (error) {
      console.error('Failed to fail task:', error)
      message.error('操作失败')
    }
  }

  const showDetail = (task) => {
    setSelectedTask(task)
    setDetailModalVisible(true)
  }

  const columns = [
    {
      title: '表计名称',
      dataIndex: 'meterId',
      key: 'meterId',
      render: (meterId) => (
        <span>
          {getMeterType(meterId) === 'ELECTRICITY' ? (
            <ThunderboltOutlined style={{ color: '#1890ff', marginRight: '4px' }} />
          ) : (
            <DropboxOutlined style={{ color: '#13c2c2', marginRight: '4px' }} />
          )}
          {getMeterName(meterId)}
        </span>
      )
    },
    {
      title: '所属企业',
      dataIndex: 'meterId',
      key: 'enterprise',
      render: (meterId) => getMeterEnterpriseName(meterId)
    },
    {
      title: '表计类型',
      dataIndex: 'meterId',
      key: 'type',
      render: (meterId) => {
        const type = getMeterType(meterId)
        return type === 'ELECTRICITY' ? (
          <Tag color="blue">电表</Tag>
        ) : (
          <Tag color="cyan">水表</Tag>
        )
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Tag color={getStatusColor(status)}>
          {getStatusText(status)}
        </Tag>
      )
    },
    {
      title: '数据缺失开始时间',
      dataIndex: 'missingFrom',
      key: 'missingFrom',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
    },
    {
      title: '数据缺失结束时间',
      dataIndex: 'missingTo',
      key: 'missingTo',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
    },
    {
      title: '完成时间',
      dataIndex: 'completedAt',
      key: 'completedAt',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => showDetail(record)}
          >
            详情
          </Button>
          {(record.status === 'PENDING' || record.status === 'IN_PROGRESS') && (
            <Button
              type="link"
              icon={<CheckCircleOutlined />}
              onClick={() => completeTask(record.id)}
            >
              完成
            </Button>
          )}
          {(record.status === 'PENDING' || record.status === 'IN_PROGRESS') && (
            <Button
              type="link"
              danger
              icon={<CloseCircleOutlined />}
              onClick={() => failTask(record.id)}
            >
              标记失败
            </Button>
          )}
        </Space>
      )
    }
  ]

  const pendingCount = tasks.filter(t => t.status === 'PENDING').length
  const inProgressCount = tasks.filter(t => t.status === 'IN_PROGRESS').length
  const completedCount = tasks.filter(t => t.status === 'COMPLETED').length
  const failedCount = tasks.filter(t => t.status === 'FAILED').length

  const filteredTasks = tasks.filter(t => {
    if (selectedStatus) {
      return t.status === selectedStatus
    }
    return true
  })

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>补采任务</h2>
        <Button icon={<ReloadOutlined />} onClick={fetchData} loading={loading}>
          刷新
        </Button>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: '16px' }}>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="待处理任务"
              value={pendingCount}
              prefix={<ClockCircleOutlined style={{ color: '#faad14' }} />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="处理中任务"
              value={inProgressCount}
              prefix={<ThunderboltOutlined style={{ color: '#1890ff' }} />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="已完成任务"
              value={completedCount}
              prefix={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="失败任务"
              value={failedCount}
              prefix={<CloseCircleOutlined style={{ color: '#ff4d4f' }} />}
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
      </Row>

      <Card style={{ marginBottom: '16px' }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} md={8}>
            <div style={{ marginBottom: '8px', fontWeight: 'bold' }}>状态筛选</div>
            <Select
              placeholder="选择状态"
              style={{ width: '100%' }}
              allowClear
              onChange={setSelectedStatus}
              value={selectedStatus}
            >
              <Select.Option value="PENDING">待处理</Select.Option>
              <Select.Option value="IN_PROGRESS">处理中</Select.Option>
              <Select.Option value="COMPLETED">已完成</Select.Option>
              <Select.Option value="CANCELLED">已取消</Select.Option>
              <Select.Option value="FAILED">失败</Select.Option>
            </Select>
          </Col>
        </Row>
      </Card>

      <Card title="任务列表">
        <Table
          columns={columns}
          dataSource={filteredTasks}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>

      <Modal
        title="补采任务详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailModalVisible(false)}>
            关闭
          </Button>
        ]}
        width={700}
      >
        {selectedTask && (
          <div>
            <Descriptions title="基本信息" bordered column={2}>
              <Descriptions.Item label="表计名称">
                {getMeterName(selectedTask.meterId)}
              </Descriptions.Item>
              <Descriptions.Item label="所属企业">
                {getMeterEnterpriseName(selectedTask.meterId)}
              </Descriptions.Item>
              <Descriptions.Item label="表计类型">
                {getMeterType(selectedTask.meterId) === 'ELECTRICITY' ? (
                  <Tag color="blue">电表</Tag>
                ) : (
                  <Tag color="cyan">水表</Tag>
                )}
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={getStatusColor(selectedTask.status)}>
                  {getStatusText(selectedTask.status)}
                </Tag>
              </Descriptions.Item>
            </Descriptions>

            <Descriptions title="时间信息" bordered column={2} style={{ marginTop: '16px' }}>
              <Descriptions.Item label="数据缺失开始时间">
                {selectedTask.missingFrom ? dayjs(selectedTask.missingFrom).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="数据缺失结束时间">
                {selectedTask.missingTo ? dayjs(selectedTask.missingTo).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="任务创建时间">
                {selectedTask.createdAt ? dayjs(selectedTask.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="任务完成时间">
                {selectedTask.completedAt ? dayjs(selectedTask.completedAt).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
            </Descriptions>

            {selectedTask.notes && (
              <Descriptions title="备注信息" bordered column={1} style={{ marginTop: '16px' }}>
                <Descriptions.Item label="备注">
                  {selectedTask.notes}
                </Descriptions.Item>
              </Descriptions>
            )}
          </div>
        )}
      </Modal>
    </div>
  )
}

export default ResampleTasks
