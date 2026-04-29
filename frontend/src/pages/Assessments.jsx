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
  Descriptions
} from 'antd'
import {
  ReloadOutlined,
  CheckCircleOutlined,
  CarryOutOutlined,
  ThunderboltOutlined,
  DropboxOutlined,
  RiseOutlined,
  EyeOutlined
} from '@ant-design/icons'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, Radar } from 'recharts'
import dayjs from 'dayjs'
import { assessmentApi, enterpriseApi } from '../api'

const Assessments = () => {
  const [assessments, setAssessments] = useState([])
  const [enterprises, setEnterprises] = useState([])
  const [loading, setLoading] = useState(false)
  const [selectedYear, setSelectedYear] = useState(dayjs().year())
  const [selectedMonth, setSelectedMonth] = useState(dayjs().month() + 1)
  const [detailModalVisible, setDetailModalVisible] = useState(false)
  const [selectedAssessment, setSelectedAssessment] = useState(null)

  useEffect(() => {
    fetchData()
  }, [selectedYear, selectedMonth])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [assessmentsRes, enterprisesRes] = await Promise.all([
        assessmentApi.getByMonth(selectedYear, selectedMonth),
        enterpriseApi.getAll()
      ])

      setAssessments(assessmentsRes.data)
      setEnterprises(enterprisesRes.data)
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

  const getLevelColor = (level) => {
    const colorMap = {
      EXCELLENT: '#52c41a',
      GOOD: '#1890ff',
      PASS: '#faad14',
      FAIL: '#ff4d4f'
    }
    return colorMap[level] || '#666'
  }

  const getLevelText = (level) => {
    const textMap = {
      EXCELLENT: '优秀',
      GOOD: '良好',
      PASS: '合格',
      FAIL: '不合格'
    }
    return textMap[level] || level
  }

  const getLevelIcon = (level) => {
    if (level === 'EXCELLENT') return <CheckCircleOutlined style={{ color: '#52c41a' }} />
    if (level === 'GOOD') return <CheckCircleOutlined style={{ color: '#1890ff' }} />
    if (level === 'PASS') return <CheckCircleOutlined style={{ color: '#faad14' }} />
    return <CheckCircleOutlined style={{ color: '#ff4d4f' }} />
  }

  const assessAll = async () => {
    try {
      await assessmentApi.assessAll(selectedYear, selectedMonth)
      message.success('节能考核已完成')
      fetchData()
    } catch (error) {
      console.error('Failed to assess:', error)
      message.error('考核失败')
    }
  }

  const showDetail = (assessment) => {
    setSelectedAssessment(assessment)
    setDetailModalVisible(true)
  }

  const columns = [
    {
      title: '企业名称',
      dataIndex: 'enterpriseId',
      key: 'enterpriseId',
      render: (enterpriseId) => getEnterpriseName(enterpriseId)
    },
    {
      title: '考核等级',
      dataIndex: 'level',
      key: 'level',
      render: (level) => (
        <Tag icon={getLevelIcon(level)} color={getLevelColor(level)}>
          {getLevelText(level)}
        </Tag>
      )
    },
    {
      title: '综合节省率',
      dataIndex: 'overallSavingRate',
      key: 'overallSavingRate',
      render: (val) => (
        <span style={{ fontWeight: 'bold', color: val > 0 ? '#52c41a' : '#ff4d4f' }}>
          {val?.toFixed(2)}%
        </span>
      )
    },
    {
      title: '用电节省率',
      dataIndex: 'electricitySavingRate',
      key: 'electricitySavingRate',
      render: (val) => `${val?.toFixed(2)}%`
    },
    {
      title: '用水节省率',
      dataIndex: 'waterSavingRate',
      key: 'waterSavingRate',
      render: (val) => `${val?.toFixed(2)}%`
    },
    {
      title: '是否达标',
      dataIndex: 'eligibleForDiscount',
      key: 'eligibleForDiscount',
      render: (eligible) => (
        <Tag color={eligible ? 'green' : 'red'}>
          {eligible ? '是 (可减免)' : '否'}
        </Tag>
      )
    },
    {
      title: '考核时间',
      dataIndex: 'assessedAt',
      key: 'assessedAt',
      render: (time) => dayjs(time).format('YYYY-MM-DD')
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button
          type="link"
          icon={<EyeOutlined />}
          onClick={() => showDetail(record)}
        >
          详情
        </Button>
      )
    }
  ]

  const years = []
  const currentYear = dayjs().year()
  for (let i = currentYear; i >= currentYear - 3; i--) {
    years.push(i)
  }

  const months = Array.from({ length: 12 }, (_, i) => i + 1)

  const excellentCount = assessments.filter(a => a.level === 'EXCELLENT').length
  const goodCount = assessments.filter(a => a.level === 'GOOD').length
  const passCount = assessments.filter(a => a.level === 'PASS').length
  const failCount = assessments.filter(a => a.level === 'FAIL').length
  const eligibleCount = assessments.filter(a => a.eligibleForDiscount).length

  const chartData = assessments.slice(0, 10).map(a => ({
    name: getEnterpriseName(a.enterpriseId),
    用电节省: a.electricitySavingRate,
    用水节省: a.waterSavingRate,
    综合节省: a.overallSavingRate
  }))

  const radarData = [
    { subject: '优秀', A: excellentCount, fullMark: assessments.length || 5 },
    { subject: '良好', A: goodCount, fullMark: assessments.length || 5 },
    { subject: '合格', A: passCount, fullMark: assessments.length || 5 },
    { subject: '不合格', A: failCount, fullMark: assessments.length || 5 }
  ]

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>节能考核</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button type="primary" icon={<CarryOutOutlined />} onClick={assessAll}>
            执行考核
          </Button>
          <Button icon={<ReloadOutlined />} onClick={fetchData} loading={loading}>
            刷新
          </Button>
        </div>
      </div>

      <Card style={{ marginBottom: '16px' }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} md={8}>
            <div style={{ marginBottom: '8px', fontWeight: 'bold' }}>年份</div>
            <Select
              value={selectedYear}
              onChange={setSelectedYear}
              style={{ width: '100%' }}
            >
              {years.map(y => (
                <Select.Option key={y} value={y}>{y}年</Select.Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8}>
            <div style={{ marginBottom: '8px', fontWeight: 'bold' }}>月份</div>
            <Select
              value={selectedMonth}
              onChange={setSelectedMonth}
              style={{ width: '100%' }}
            >
              {months.map(m => (
                <Select.Option key={m} value={m}>{m}月</Select.Option>
              ))}
            </Select>
          </Col>
        </Row>
      </Card>

      <Row gutter={[16, 16]} style={{ marginBottom: '16px' }}>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="优秀企业"
              value={excellentCount}
              prefix={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="良好企业"
              value={goodCount}
              prefix={<CheckCircleOutlined style={{ color: '#1890ff' }} />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="合格企业"
              value={passCount}
              prefix={<CheckCircleOutlined style={{ color: '#faad14' }} />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={6}>
          <Card>
            <Statistic
              title="可减免企业"
              value={eligibleCount}
              prefix={<RiseOutlined style={{ color: '#52c41a' }} />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
      </Row>

      {chartData.length > 0 && (
        <Row gutter={[16, 16]} style={{ marginBottom: '16px' }}>
          <Col xs={24} lg={16}>
            <Card title="节能节省率排行 (前10名)">
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="用电节省" fill="#1890ff" name="用电节省率 (%)" />
                  <Bar dataKey="用水节省" fill="#13c2c2" name="用水节省率 (%)" />
                  <Bar dataKey="综合节省" fill="#52c41a" name="综合节省率 (%)" />
                </BarChart>
              </ResponsiveContainer>
            </Card>
          </Col>
          <Col xs={24} lg={8}>
            <Card title="等级分布">
              <ResponsiveContainer width="100%" height={300}>
                <RadarChart data={radarData}>
                  <PolarGrid />
                  <PolarAngleAxis dataKey="subject" />
                  <PolarRadiusAxis />
                  <Radar name="企业数" dataKey="A" stroke="#1890ff" fill="#1890ff" fillOpacity={0.6} />
                </RadarChart>
              </ResponsiveContainer>
            </Card>
          </Col>
        </Row>
      )}

      <Card title="考核详情列表">
        <Table
          columns={columns}
          dataSource={assessments}
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
        title="考核详情"
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailModalVisible(false)}>
            关闭
          </Button>
        ]}
        width={700}
      >
        {selectedAssessment && (
          <div>
            <Descriptions title="企业信息" bordered column={2}>
              <Descriptions.Item label="企业名称">
                {getEnterpriseName(selectedAssessment.enterpriseId)}
              </Descriptions.Item>
              <Descriptions.Item label="考核等级">
                <Tag color={getLevelColor(selectedAssessment.level)}>
                  {getLevelText(selectedAssessment.level)}
                </Tag>
              </Descriptions.Item>
            </Descriptions>

            <Descriptions title="用电指标" bordered column={2} style={{ marginTop: '16px' }}>
              <Descriptions.Item label="用电量">
                {selectedAssessment.electricityConsumption?.toFixed(2)} kWh
              </Descriptions.Item>
              <Descriptions.Item label="用电配额">
                {selectedAssessment.electricityQuota?.toFixed(2)} kWh
              </Descriptions.Item>
              <Descriptions.Item label="用电节省率" span={2}>
                <span style={{ 
                  fontWeight: 'bold', 
                  color: selectedAssessment.electricitySavingRate > 0 ? '#52c41a' : '#ff4d4f' 
                }}>
                  {selectedAssessment.electricitySavingRate?.toFixed(2)}%
                </span>
              </Descriptions.Item>
            </Descriptions>

            <Descriptions title="用水指标" bordered column={2} style={{ marginTop: '16px' }}>
              <Descriptions.Item label="用水量">
                {selectedAssessment.waterConsumption?.toFixed(2)} 吨
              </Descriptions.Item>
              <Descriptions.Item label="用水配额">
                {selectedAssessment.waterQuota?.toFixed(2)} 吨
              </Descriptions.Item>
              <Descriptions.Item label="用水节省率" span={2}>
                <span style={{ 
                  fontWeight: 'bold', 
                  color: selectedAssessment.waterSavingRate > 0 ? '#52c41a' : '#ff4d4f' 
                }}>
                  {selectedAssessment.waterSavingRate?.toFixed(2)}%
                </span>
              </Descriptions.Item>
            </Descriptions>

            <Descriptions title="综合评估" bordered column={2} style={{ marginTop: '16px' }}>
              <Descriptions.Item label="综合节省率">
                <span style={{ 
                  fontWeight: 'bold', 
                  fontSize: '18px',
                  color: selectedAssessment.overallSavingRate > 0 ? '#52c41a' : '#ff4d4f' 
                }}>
                  {selectedAssessment.overallSavingRate?.toFixed(2)}%
                </span>
              </Descriptions.Item>
              <Descriptions.Item label="是否达标">
                <Tag color={selectedAssessment.eligibleForDiscount ? 'green' : 'red'}>
                  {selectedAssessment.eligibleForDiscount ? '是 (可减免)' : '否'}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="考核时间" span={2}>
                {dayjs(selectedAssessment.assessedAt).format('YYYY-MM-DD HH:mm:ss')}
              </Descriptions.Item>
            </Descriptions>
          </div>
        )}
      </Modal>
    </div>
  )
}

export default Assessments
