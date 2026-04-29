import React, { useState, useEffect } from 'react'
import {
  Table,
  Card,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Switch,
  Tag,
  Space,
  message,
  Popconfirm,
  Select,
  Row,
  Col,
  Statistic
} from 'antd'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  ReloadOutlined,
  EyeOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { enterpriseApi, meterApi } from '../api'

const { Option } = Select

const Enterprises = () => {
  const [enterprises, setEnterprises] = useState([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingEnterprise, setEditingEnterprise] = useState(null)
  const [detailModalVisible, setDetailModalVisible] = useState(false)
  const [selectedEnterprise, setSelectedEnterprise] = useState(null)
  const [enterpriseMeters, setEnterpriseMeters] = useState([])
  const [form] = Form.useForm()

  useEffect(() => {
    fetchEnterprises()
  }, [])

  const fetchEnterprises = async () => {
    setLoading(true)
    try {
      const response = await enterpriseApi.getAll()
      setEnterprises(response.data)
    } catch (error) {
      console.error('Failed to fetch enterprises:', error)
      message.error('加载企业列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleAdd = () => {
    setEditingEnterprise(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingEnterprise(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await enterpriseApi.delete(id)
      message.success('删除成功')
      fetchEnterprises()
    } catch (error) {
      console.error('Failed to delete enterprise:', error)
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()

      if (editingEnterprise) {
        await enterpriseApi.update(editingEnterprise.id, values)
        message.success('更新成功')
      } else {
        await enterpriseApi.create(values)
        message.success('创建成功')
      }

      setModalVisible(false)
      fetchEnterprises()
    } catch (error) {
      console.error('Failed to save enterprise:', error)
      message.error('保存失败')
    }
  }

  const handleViewDetail = async (record) => {
    setSelectedEnterprise(record)
    try {
      const response = await meterApi.getByEnterprise(record.id)
      setEnterpriseMeters(response.data)
    } catch (error) {
      console.error('Failed to fetch meters:', error)
      setEnterpriseMeters([])
    }
    setDetailModalVisible(true)
  }

  const columns = [
    {
      title: '企业名称',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <a onClick={() => handleViewDetail(record)}>{text}</a>
      )
    },
    {
      title: '行业类型',
      dataIndex: 'industryType',
      key: 'industryType',
      render: (type) => <Tag color="blue">{type}</Tag>
    },
    {
      title: '月用电配额 (kWh)',
      dataIndex: 'monthlyElectricityQuota',
      key: 'monthlyElectricityQuota',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '月用水配额 (吨)',
      dataIndex: 'monthlyWaterQuota',
      key: 'monthlyWaterQuota',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '重点企业',
      dataIndex: 'isKeyEnterprise',
      key: 'isKeyEnterprise',
      render: (val) => (
        <Tag color={val ? 'red' : 'default'}>
          {val ? '是' : '否'}
        </Tag>
      )
    },
    {
      title: '服务限制',
      dataIndex: 'servicesRestricted',
      key: 'servicesRestricted',
      render: (val) => (
        <Tag color={val ? 'orange' : 'green'}>
          {val ? '已限制' : '正常'}
        </Tag>
      )
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (time) => dayjs(time).format('YYYY-MM-DD HH:mm')
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确定要删除该企业吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  const meterColumns = [
    {
      title: '表计名称',
      dataIndex: 'name',
      key: 'name'
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => (
        <Tag color={type === 'ELECTRICITY' ? 'blue' : 'cyan'}>
          {type === 'ELECTRICITY' ? '电表' : '水表'}
        </Tag>
      )
    },
    {
      title: '位置',
      dataIndex: 'location',
      key: 'location'
    },
    {
      title: '最新读数',
      dataIndex: 'lastReading',
      key: 'lastReading',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '读数时间',
      dataIndex: 'lastReadingTime',
      key: 'lastReadingTime',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
    },
    {
      title: '状态',
      dataIndex: 'active',
      key: 'active',
      render: (val) => (
        <Tag color={val ? 'green' : 'red'}>
          {val ? '正常' : '停用'}
        </Tag>
      )
    }
  ]

  return (
    <div>
      <Card
        title="企业管理"
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={fetchEnterprises} loading={loading}>
              刷新
            </Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增企业
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={enterprises}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingEnterprise ? '编辑企业' : '新增企业'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={700}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            isKeyEnterprise: false,
            servicesRestricted: false
          }}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="name"
                label="企业名称"
                rules={[{ required: true, message: '请输入企业名称' }]}
              >
                <Input placeholder="请输入企业名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="industryType"
                label="行业类型"
                rules={[{ required: true, message: '请选择行业类型' }]}
              >
                <Select placeholder="请选择行业类型">
                  <Option value="制造业">制造业</Option>
                  <Option value="服务业">服务业</Option>
                  <Option value="物流">物流</Option>
                  <Option value="科研">科研</Option>
                  <Option value="其他">其他</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="monthlyElectricityQuota"
                label="月用电配额 (kWh)"
                rules={[{ required: true, message: '请输入月用电配额' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="请输入月用电配额"
                  min={0}
                  precision={2}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="monthlyWaterQuota"
                label="月用水配额 (吨)"
                rules={[{ required: true, message: '请输入月用水配额' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="请输入月用水配额"
                  min={0}
                  precision={2}
                />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="dailyElectricityQuota"
                label="日用电配额 (kWh)"
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="请输入日用电配额"
                  min={0}
                  precision={2}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="dailyWaterQuota"
                label="日用水配额 (吨)"
              >
                <InputNumber
                  style={{ width: '100%' }}
                  placeholder="请输入日用水配额"
                  min={0}
                  precision={2}
                />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="isKeyEnterprise"
                label="重点企业"
                valuePropName="checked"
              >
                <Switch checkedChildren="是" unCheckedChildren="否" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="servicesRestricted"
                label="服务限制"
                valuePropName="checked"
              >
                <Switch checkedChildren="限制" unCheckedChildren="正常" />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>

      <Modal
        title="企业详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={900}
      >
        {selectedEnterprise && (
          <div>
            <Card size="small" style={{ marginBottom: '16px' }}>
              <Row gutter={16}>
                <Col span={6}>
                  <Statistic title="企业名称" value={selectedEnterprise.name} />
                </Col>
                <Col span={6}>
                  <Statistic title="行业类型" value={selectedEnterprise.industryType} />
                </Col>
                <Col span={6}>
                  <Statistic
                    title="重点企业"
                    value={selectedEnterprise.isKeyEnterprise ? '是' : '否'}
                  />
                </Col>
                <Col span={6}>
                  <Statistic
                    title="服务状态"
                    value={selectedEnterprise.servicesRestricted ? '已限制' : '正常'}
                  />
                </Col>
              </Row>
              <Row gutter={16} style={{ marginTop: '16px' }}>
                <Col span={6}>
                  <Statistic
                    title="月用电配额"
                    value={selectedEnterprise.monthlyElectricityQuota?.toFixed(2)}
                    suffix="kWh"
                  />
                </Col>
                <Col span={6}>
                  <Statistic
                    title="月用水配额"
                    value={selectedEnterprise.monthlyWaterQuota?.toFixed(2)}
                    suffix="吨"
                  />
                </Col>
                <Col span={6}>
                  <Statistic
                    title="日用电配额"
                    value={selectedEnterprise.dailyElectricityQuota?.toFixed(2) || '-'}
                    suffix="kWh"
                  />
                </Col>
                <Col span={6}>
                  <Statistic
                    title="日用水配额"
                    value={selectedEnterprise.dailyWaterQuota?.toFixed(2) || '-'}
                    suffix="吨"
                  />
                </Col>
              </Row>
            </Card>

            <Card title="关联表计" size="small">
              <Table
                columns={meterColumns}
                dataSource={enterpriseMeters}
                rowKey="id"
                size="small"
                pagination={false}
              />
            </Card>
          </div>
        )}
      </Modal>
    </div>
  )
}

export default Enterprises
