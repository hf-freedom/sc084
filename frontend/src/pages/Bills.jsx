import React, { useState, useEffect } from 'react'
import {
  Table,
  Card,
  Button,
  Tag,
  Space,
  message,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  Row,
  Col,
  Statistic,
  Descriptions,
  Popconfirm
} from 'antd'
import {
  ReloadOutlined,
  PlusOutlined,
  CheckOutlined,
  CreditCardOutlined,
  FileAddOutlined,
  EyeOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { billApi, enterpriseApi } from '../api'

const { Option } = Select

const Bills = () => {
  const [bills, setBills] = useState([])
  const [enterprises, setEnterprises] = useState([])
  const [loading, setLoading] = useState(false)
  const [detailModalVisible, setDetailModalVisible] = useState(false)
  const [selectedBill, setSelectedBill] = useState(null)
  const [payModalVisible, setPayModalVisible] = useState(false)
  const [adjustmentModalVisible, setAdjustmentModalVisible] = useState(false)
  const [generateModalVisible, setGenerateModalVisible] = useState(false)
  const [form] = Form.useForm()
  const [payForm] = Form.useForm()
  const [adjustmentForm] = Form.useForm()

  useEffect(() => {
    fetchBills()
    fetchEnterprises()
  }, [])

  const fetchBills = async () => {
    setLoading(true)
    try {
      const response = await billApi.getAll()
      setBills(response.data)
    } catch (error) {
      console.error('Failed to fetch bills:', error)
      message.error('加载账单列表失败')
    } finally {
      setLoading(false)
    }
  }

  const fetchEnterprises = async () => {
    try {
      const response = await enterpriseApi.getAll()
      setEnterprises(response.data)
    } catch (error) {
      console.error('Failed to fetch enterprises:', error)
    }
  }

  const getStatusColor = (status) => {
    const colorMap = {
      DRAFT: 'default',
      CONFIRMED: 'processing',
      PARTIALLY_PAID: 'warning',
      PAID: 'success',
      OVERDUE: 'error'
    }
    return colorMap[status] || 'default'
  }

  const getStatusText = (status) => {
    const textMap = {
      DRAFT: '草稿',
      CONFIRMED: '已确认',
      PARTIALLY_PAID: '部分支付',
      PAID: '已支付',
      OVERDUE: '已逾期'
    }
    return textMap[status] || status
  }

  const handleGenerate = async () => {
    try {
      const values = await form.validateFields()
      await billApi.generate(values.enterpriseId, values.year, values.month)
      message.success('账单生成成功')
      setGenerateModalVisible(false)
      form.resetFields()
      fetchBills()
    } catch (error) {
      console.error('Failed to generate bill:', error)
      message.error('生成账单失败')
    }
  }

  const handleConfirm = async (id) => {
    try {
      await billApi.confirm(id, 'admin')
      message.success('账单确认成功')
      fetchBills()
    } catch (error) {
      console.error('Failed to confirm bill:', error)
      message.error('确认账单失败')
    }
  }

  const handlePay = async () => {
    try {
      const values = await payForm.validateFields()
      await billApi.pay(selectedBill.id, values.amount)
      message.success('支付成功')
      setPayModalVisible(false)
      payForm.resetFields()
      fetchBills()
    } catch (error) {
      console.error('Failed to pay bill:', error)
      message.error('支付失败')
    }
  }

  const handleCreateAdjustment = async () => {
    try {
      const values = await adjustmentForm.validateFields()
      await billApi.createAdjustment(
        selectedBill.id,
        values.type,
        values.amount,
        values.reason,
        'admin'
      )
      message.success('调整单创建成功')
      setAdjustmentModalVisible(false)
      adjustmentForm.resetFields()
      fetchBills()
    } catch (error) {
      console.error('Failed to create adjustment:', error)
      message.error('创建调整单失败')
    }
  }

  const handleViewDetail = (record) => {
    setSelectedBill(record)
    setDetailModalVisible(true)
  }

  const handlePayModal = (record) => {
    setSelectedBill(record)
    payForm.setFieldsValue({ amount: record.finalAmount ? record.finalAmount - (record.paidAmount || 0) : 0 })
    setPayModalVisible(true)
  }

  const handleAdjustmentModal = (record) => {
    setSelectedBill(record)
    adjustmentForm.resetFields()
    setAdjustmentModalVisible(true)
  }

  const columns = [
    {
      title: '月份',
      key: 'month',
      render: (_, record) => `${record.year}年${record.month}月`,
      sorter: (a, b) => {
        if (a.year !== b.year) return a.year - b.year
        return a.month - b.month
      }
    },
    {
      title: '企业ID',
      dataIndex: 'enterpriseId',
      key: 'enterpriseId',
      ellipsis: true
    },
    {
      title: '用电 (kWh)',
      dataIndex: 'electricityConsumption',
      key: 'electricityConsumption',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '用水 (吨)',
      dataIndex: 'waterConsumption',
      key: 'waterConsumption',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '总金额 (元)',
      dataIndex: 'finalAmount',
      key: 'finalAmount',
      render: (val) => <strong>¥{val?.toFixed(2)}</strong>,
      sorter: (a, b) => (a.finalAmount || 0) - (b.finalAmount || 0)
    },
    {
      title: '已支付 (元)',
      dataIndex: 'paidAmount',
      key: 'paidAmount',
      render: (val) => `¥${(val || 0).toFixed(2)}`
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => <Tag color={getStatusColor(status)}>{getStatusText(status)}</Tag>,
      filters: [
        { text: '草稿', value: 'DRAFT' },
        { text: '已确认', value: 'CONFIRMED' },
        { text: '部分支付', value: 'PARTIALLY_PAID' },
        { text: '已支付', value: 'PAID' },
        { text: '已逾期', value: 'OVERDUE' }
      ],
      onFilter: (value, record) => record.status === value
    },
    {
      title: '生成时间',
      dataIndex: 'generatedAt',
      key: 'generatedAt',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          {record.status === 'DRAFT' && (
            <Popconfirm
              title="确定要确认该账单吗？确认后不可直接修改，只能通过调整单调整。"
              onConfirm={() => handleConfirm(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" icon={<CheckOutlined />}>
                确认
              </Button>
            </Popconfirm>
          )}
          {(record.status === 'CONFIRMED' || record.status === 'PARTIALLY_PAID' || record.status === 'OVERDUE') && (
            <>
              <Button
                type="link"
                icon={<CreditCardOutlined />}
                onClick={() => handlePayModal(record)}
              >
                支付
              </Button>
              <Button
                type="link"
                icon={<FileAddOutlined />}
                onClick={() => handleAdjustmentModal(record)}
              >
                调整
              </Button>
            </>
          )}
        </Space>
      )
    }
  ]

  return (
    <div>
      <Card
        title="账单管理"
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={fetchBills} loading={loading}>
              刷新
            </Button>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                form.resetFields()
                setGenerateModalVisible(true)
              }}
            >
              生成账单
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={bills}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="生成账单"
        open={generateModalVisible}
        onOk={handleGenerate}
        onCancel={() => setGenerateModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="enterpriseId"
            label="选择企业"
            rules={[{ required: true, message: '请选择企业' }]}
          >
            <Select placeholder="请选择企业" showSearch optionFilterProp="children">
              {enterprises.map(e => (
                <Option key={e.id} value={e.id}>{e.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="year"
            label="年份"
            rules={[{ required: true, message: '请选择年份' }]}
          >
            <Select placeholder="请选择年份">
              {[2024, 2025, 2026].map(year => (
                <Option key={year} value={year}>{year}年</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="month"
            label="月份"
            rules={[{ required: true, message: '请选择月份' }]}
          >
            <Select placeholder="请选择月份">
              {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map(month => (
                <Option key={month} value={month}>{month}月</Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="账单详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        width={900}
      >
        {selectedBill && (
          <div>
            <Card size="small" style={{ marginBottom: 16 }}>
              <Descriptions column={4} size="small">
                <Descriptions.Item label="月份">{selectedBill.year}年{selectedBill.month}月</Descriptions.Item>
                <Descriptions.Item label="状态">
                  <Tag color={getStatusColor(selectedBill.status)}>{getStatusText(selectedBill.status)}</Tag>
                </Descriptions.Item>
                <Descriptions.Item label="生成时间">
                  {selectedBill.generatedAt ? dayjs(selectedBill.generatedAt).format('YYYY-MM-DD HH:mm') : '-'}
                </Descriptions.Item>
                <Descriptions.Item label="确认时间">
                  {selectedBill.confirmedAt ? dayjs(selectedBill.confirmedAt).format('YYYY-MM-DD HH:mm') : '-'}
                </Descriptions.Item>
              </Descriptions>
            </Card>

            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="用电量 (kWh)" value={selectedBill.electricityConsumption?.toFixed(2)} />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="用水量 (吨)" value={selectedBill.waterConsumption?.toFixed(2)} />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="超额用电 (kWh)" value={selectedBill.electricityOverQuota?.toFixed(2)} />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="超额用水 (吨)" value={selectedBill.waterOverQuota?.toFixed(2)} />
                </Card>
              </Col>
            </Row>

            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="基础电费 (元)" value={selectedBill.basicElectricityCost?.toFixed(2)} prefix="¥" />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="基础水费 (元)" value={selectedBill.basicWaterCost?.toFixed(2)} prefix="¥" />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="超额电费 (元)" value={selectedBill.overQuotaElectricityCost?.toFixed(2)} prefix="¥" />
                </Card>
              </Col>
              <Col span={6}>
                <Card size="small">
                  <Statistic title="超额水费 (元)" value={selectedBill.overQuotaWaterCost?.toFixed(2)} prefix="¥" />
                </Card>
              </Col>
            </Row>

            <Row gutter={16}>
              <Col span={8}>
                <Card size="small" type="inner" title="账单金额">
                  <Statistic title="总金额" value={selectedBill.totalAmount?.toFixed(2)} prefix="¥" />
                </Card>
              </Col>
              <Col span={8}>
                <Card size="small" type="inner" title="优惠/调整">
                  <Statistic title="优惠金额" value={selectedBill.discountAmount?.toFixed(2)} prefix="¥" />
                </Card>
              </Col>
              <Col span={8}>
                <Card size="small" type="inner" title="应付金额">
                  <Statistic
                    title="应付金额"
                    value={selectedBill.finalAmount?.toFixed(2)}
                    prefix="¥"
                    valueStyle={{ color: '#cf1322' }}
                  />
                </Card>
              </Col>
            </Row>

            {selectedBill.adjustments && selectedBill.adjustments.length > 0 && (
              <Card title="调整记录" size="small" style={{ marginTop: 16 }}>
                <Table
                  dataSource={selectedBill.adjustments}
                  rowKey="id"
                  size="small"
                  pagination={false}
                  columns={[
                    {
                      title: '类型',
                      dataIndex: 'type',
                      key: 'type',
                      render: (type) => {
                        const typeMap = {
                          MANUAL_ADD: '人工增加',
                          MANUAL_DEDUCT: '人工扣减',
                          ENERGY_SAVING_REWARD: '节能奖励',
                          OVERDUE_PENALTY: '逾期罚款',
                          OTHER: '其他'
                        }
                        return <Tag>{typeMap[type] || type}</Tag>
                      }
                    },
                    {
                      title: '金额',
                      dataIndex: 'amount',
                      key: 'amount',
                      render: (val, record) => (
                        <span style={{ color: (record.type === 'MANUAL_DEDUCT' || record.type === 'ENERGY_SAVING_REWARD') ? '#3f8600' : '#cf1322' }}>
                          {(record.type === 'MANUAL_DEDUCT' || record.type === 'ENERGY_SAVING_REWARD') ? '-' : '+'}¥{val?.toFixed(2)}
                        </span>
                      )
                    },
                    {
                      title: '原因',
                      dataIndex: 'reason',
                      key: 'reason'
                    },
                    {
                      title: '操作人',
                      dataIndex: 'createdBy',
                      key: 'createdBy'
                    },
                    {
                      title: '创建时间',
                      dataIndex: 'createdAt',
                      key: 'createdAt',
                      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
                    }
                  ]}
                />
              </Card>
            )}
          </div>
        )}
      </Modal>

      <Modal
        title="支付账单"
        open={payModalVisible}
        onOk={handlePay}
        onCancel={() => setPayModalVisible(false)}
      >
        <Form form={payForm} layout="vertical">
          <Form.Item
            name="amount"
            label="支付金额 (元)"
            rules={[{ required: true, message: '请输入支付金额' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="请输入支付金额"
              min={0}
              precision={2}
            />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="创建调整单"
        open={adjustmentModalVisible}
        onOk={handleCreateAdjustment}
        onCancel={() => setAdjustmentModalVisible(false)}
      >
        <Form form={adjustmentForm} layout="vertical">
          <Form.Item
            name="type"
            label="调整类型"
            rules={[{ required: true, message: '请选择调整类型' }]}
          >
            <Select placeholder="请选择调整类型">
              <Option value="MANUAL_ADD">人工增加</Option>
              <Option value="MANUAL_DEDUCT">人工扣减</Option>
              <Option value="ENERGY_SAVING_REWARD">节能奖励</Option>
              <Option value="OVERDUE_PENALTY">逾期罚款</Option>
              <Option value="OTHER">其他</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="amount"
            label="调整金额 (元)"
            rules={[{ required: true, message: '请输入调整金额' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="请输入调整金额"
              min={0}
              precision={2}
            />
          </Form.Item>
          <Form.Item
            name="reason"
            label="调整原因"
            rules={[{ required: true, message: '请输入调整原因' }]}
          >
            <Input.TextArea placeholder="请输入调整原因" rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default Bills
