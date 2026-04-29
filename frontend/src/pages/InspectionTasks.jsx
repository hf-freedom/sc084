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
  Input,
  Form,
  Space,
  Descriptions
} from 'antd'
import {
  ReloadOutlined,
  ToolOutlined,
  PlayCircleOutlined,
  CheckCircleOutlined,
  StopOutlined,
  UserOutlined,
  EyeOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { inspectionTaskApi, enterpriseApi, alertApi } from '../api'

const { TextArea } = Input

const InspectionTasks = () => {
  const [tasks, setTasks] = useState([])
  const [enterprises, setEnterprises] = useState([])
  const [alerts, setAlerts] = useState([])
  const [loading, setLoading] = useState(false)
  const [selectedStatus, setSelectedStatus] = useState(null)
  const [selectedPriority, setSelectedPriority] = useState(null)
  const [detailModalVisible, setDetailModalVisible] = useState(false)
  const [selectedTask, setSelectedTask] = useState(null)
  const [assignModalVisible, setAssignModalVisible] = useState(false)
  const [completeModalVisible, setCompleteModalVisible] = useState(false)
  const [form] = Form.useForm()

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [tasksRes, enterprisesRes, alertsRes] = await Promise.all([
        inspectionTaskApi.getAll(),
        enterpriseApi.getAll(),
        alertApi.getAll()
      ])

      setTasks(tasksRes.data)
      setEnterprises(enterprisesRes.data)
      setAlerts(alertsRes.data)
    } catch (error) {
      console.error('Failed to fetch data:', error)
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const getEnterpriseName = (enterpriseId) => {
    const enterprise = enterprises.find(e => e.id === enterpriseId)
    return enterprise ? enterprise.name : enterpriseId
  }

  const getAlertTitle = (alertId) => {
    const alert = alerts.find(a => a.id === alertId)
    return alert ? alert.title : '-'
  }

  const getStatusColor = (status) => {
    const colorMap = {
      PENDING: 'orange',
      ASSIGNED: 'blue',
      IN_PROGRESS: 'processing',
      COMPLETED: 'green',
      CANCELLED: 'default'
    }
    return colorMap[status] || 'default'
  }

  const getStatusText = (status) => {
    const textMap = {
      PENDING: '待处理',
      ASSIGNED: '已分配',
      IN_PROGRESS: '处理中',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    }
    return textMap[status] || status
  }

  const getPriorityColor = (priority) => {
    const colorMap = {
      LOW: 'default',
      MEDIUM: 'blue',
      HIGH: 'orange',
      URGENT: 'red'
    }
    return colorMap[priority] || 'default'
  }

  const getPriorityText = (priority) => {
    const textMap = {
      LOW: '低',
      MEDIUM: '中',
      HIGH: '高',
      URGENT: '紧急'
    }
    return textMap[priority] || priority
  }

  const assignTask = async (taskId, assignedTo) => {
    try {
      await inspectionTaskApi.assign(taskId, assignedTo)
      message.success('任务已分配')
      setAssignModalVisible(false)
      form.resetFields()
      fetchData()
    } catch (error) {
      console.error('Failed to assign task:', error)
      message.error('分配失败')
    }
  }

  const startTask = async (taskId) => {
    try {
      await inspectionTaskApi.start(taskId)
      message.success('任务已开始')
      fetchData()
    } catch (error) {
      console.error('Failed to start task:', error)
      message.error('开始失败')
    }
  }

  const completeTask = async (taskId, result) => {
    try {
      await inspectionTaskApi.complete(taskId, result)
      message.success('任务已完成')
      setCompleteModalVisible(false)
      form.resetFields()
      fetchData()
    } catch (error) {
      console.error('Failed to complete task:', error)
      message.error('完成失败')
    }
  }

  const cancelTask = async (taskId, reason) => {
    try {
      await inspectionTaskApi.cancel(taskId, reason)
      message.success('任务已取消')
      fetchData()
    } catch (error) {
      console.error('Failed to cancel task:', error)
      message.error('取消失败')
    }
  }

  const showDetail = (task) => {
    setSelectedTask(task)
    setDetailModalVisible(true)
  }

  const showAssignModal = (task) => {
    setSelectedTask(task)
    setAssignModalVisible(true)
  }

  const showCompleteModal = (task) => {
    setSelectedTask(task)
    setCompleteModalVisible(true)
  }

  const columns = [
    {
      title: '企业名称',
      dataIndex: 'enterpriseId',
      key: 'enterpriseId',
      render: (enterpriseId) => getEnterpriseName(enterpriseId)
    },
    {
      title: '任务标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      render: (priority) => (
        <Tag color={getPriorityColor(priority)}>
          {getPriorityText(priority)}
        </Tag>
      )
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
      title: '分配人',
      dataIndex: 'assignedTo',
      key: 'assignedTo',
      render: (assignedTo) => assignedTo || '-'
    },
    {
      title: '相关告警',
      dataIndex: 'relatedAlertId',
      key: 'relatedAlertId',
      render: (relatedAlertId) => relatedAlertId ? (
        <Tag color="blue">{getAlertTitle(relatedAlertId)}</Tag>
      ) : '-'
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
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
          {record.status === 'PENDING' && (
            <Button
              type="link"
              icon={<UserOutlined />}
              onClick={() => showAssignModal(record)}
            >
              分配
            </Button>
          )}
          {record.status === 'ASSIGNED' && (
            <Button
              type="link"
              icon={<PlayCircleOutlined />}
              onClick={() => startTask(record.id)}
            >
              开始
            </Button>
          )}
          {record.status === 'IN_PROGRESS' && (
            <Button
              type="link"
              icon={<CheckCircleOutlined />}
              onClick={() => showCompleteModal(record)}
            >
              完成
            </Button>
          )}
          {(record.status === 'PENDING' || record.status === 'ASSIGNED') && (
            <Button
              type="link"
              danger
              icon={<StopOutlined />}
              onClick={() => cancelTask(record.id, '手动取消')}
            >
              取消
            </Button>
          )}
        </Space>
      )
    }
  ]

  const pendingCount = tasks.filter(t => t.status === 'PENDING' || t.status === 'ASSIGNED').length
  const inProgressCount = tasks.filter(t => t.status === 'IN_PROGRESS').length
  const completedCount = tasks.filter(t => t.status === 'COMPLETED').length
  const urgentCount = tasks.filter(t => t.priority === 'URGENT' && t.status !== 'COMPLETED' && t.status !== 'CANCELLED').length

  const filteredTasks = tasks.filter(t => {
    let match = true
    if (selectedStatus) {
      match = match && t.status === selectedStatus
    }
    if (selectedPriority) {
      match = match && t.priority === selectedPriority
    }
    return match
  })

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>巡检任务</h2>
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
              prefix={<ToolOutlined style={{ color: '#faad14' }} />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="处理中任务"
              value={inProgressCount}
              prefix={<PlayCircleOutlined style={{ color: '#1890ff' }} />}
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
              title="紧急任务"
              value={urgentCount}
              prefix={<ToolOutlined style={{ color: '#ff4d4f' }} />}
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
              <Select.Option value="ASSIGNED">已分配</Select.Option>
              <Select.Option value="IN_PROGRESS">处理中</Select.Option>
              <Select.Option value="COMPLETED">已完成</Select.Option>
              <Select.Option value="CANCELLED">已取消</Select.Option>
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8}>
            <div style={{ marginBottom: '8px', fontWeight: 'bold' }}>优先级筛选</div>
            <Select
              placeholder="选择优先级"
              style={{ width: '100%' }}
              allowClear
              onChange={setSelectedPriority}
              value={selectedPriority}
            >
              <Select.Option value="LOW">低</Select.Option>
              <Select.Option value="MEDIUM">中</Select.Option>
              <Select.Option value="HIGH">高</Select.Option>
              <Select.Option value="URGENT">紧急</Select.Option>
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
        title="任务详情"
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
              <Descriptions.Item label="企业名称">
                {getEnterpriseName(selectedTask.enterpriseId)}
              </Descriptions.Item>
              <Descriptions.Item label="任务标题">
                {selectedTask.title}
              </Descriptions.Item>
              <Descriptions.Item label="优先级">
                <Tag color={getPriorityColor(selectedTask.priority)}>
                  {getPriorityText(selectedTask.priority)}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={getStatusColor(selectedTask.status)}>
                  {getStatusText(selectedTask.status)}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="分配人">
                {selectedTask.assignedTo || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="相关告警">
                {selectedTask.relatedAlertId ? getAlertTitle(selectedTask.relatedAlertId) : '-'}
              </Descriptions.Item>
            </Descriptions>

            <Descriptions title="任务描述" bordered column={1} style={{ marginTop: '16px' }}>
              <Descriptions.Item label="描述">
                {selectedTask.description || '-'}
              </Descriptions.Item>
            </Descriptions>

            {selectedTask.result && (
              <Descriptions title="处理结果" bordered column={1} style={{ marginTop: '16px' }}>
                <Descriptions.Item label="结果">
                  {selectedTask.result}
                </Descriptions.Item>
              </Descriptions>
            )}

            <Descriptions title="时间信息" bordered column={2} style={{ marginTop: '16px' }}>
              <Descriptions.Item label="创建时间">
                {selectedTask.createdAt ? dayjs(selectedTask.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="开始时间">
                {selectedTask.startedAt ? dayjs(selectedTask.startedAt).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="完成时间" span={2}>
                {selectedTask.completedAt ? dayjs(selectedTask.completedAt).format('YYYY-MM-DD HH:mm:ss') : '-'}
              </Descriptions.Item>
            </Descriptions>
          </div>
        )}
      </Modal>

      <Modal
        title="分配任务"
        open={assignModalVisible}
        onCancel={() => {
          setAssignModalVisible(false)
          form.resetFields()
        }}
        onOk={() => {
          form.validateFields().then(values => {
            assignTask(selectedTask.id, values.assignedTo)
          })
        }}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="assignedTo"
            label="分配人"
            rules={[{ required: true, message: '请输入分配人' }]}
          >
            <Input placeholder="请输入分配人姓名" prefix={<UserOutlined />} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="完成任务"
        open={completeModalVisible}
        onCancel={() => {
          setCompleteModalVisible(false)
          form.resetFields()
        }}
        onOk={() => {
          form.validateFields().then(values => {
            completeTask(selectedTask.id, values.result)
          })
        }}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="result"
            label="处理结果"
            rules={[{ required: true, message: '请输入处理结果' }]}
          >
            <TextArea
              rows={4}
              placeholder="请输入处理结果描述"
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default InspectionTasks
