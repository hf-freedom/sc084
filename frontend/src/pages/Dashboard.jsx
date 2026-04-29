import React, { useState, useEffect } from 'react'
import {
  Card,
  Row,
  Col,
  Statistic,
  Table,
  Tag,
  Space,
  Button,
  message
} from 'antd'
import {
  ShopOutlined,
  ThunderboltOutlined,
  DropboxOutlined,
  AlertOutlined,
  FileTextOutlined,
  TrophyOutlined,
  ReloadOutlined
} from '@ant-design/icons'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import dayjs from 'dayjs'
import { enterpriseApi, meterReadingApi, alertApi, billApi, meterApi } from '../api'

const Dashboard = () => {
  const [enterprises, setEnterprises] = useState([])
  const [activeAlerts, setActiveAlerts] = useState([])
  const [recentReadings, setRecentReadings] = useState([])
  const [loading, setLoading] = useState(false)
  const [consumptionData, setConsumptionData] = useState([])
  const [industryData, setIndustryData] = useState([])

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [enterpriseRes, alertsRes, metersRes] = await Promise.all([
        enterpriseApi.getAll(),
        alertApi.getActive(),
        meterApi.getActive()
      ])

      setEnterprises(enterpriseRes.data)
      setActiveAlerts(alertsRes.data)

      const industryCount = {}
      enterpriseRes.data.forEach(e => {
        industryCount[e.industryType] = (industryCount[e.industryType] || 0) + 1
      })
      setIndustryData(Object.keys(industryCount).map(key => ({
        name: key,
        value: industryCount[key]
      })))

      const last7Days = []
      for (let i = 6; i >= 0; i--) {
        const date = dayjs().subtract(i, 'day')
        last7Days.push({
          date: date.format('MM-DD'),
          electricity: Math.floor(Math.random() * 500 + 100),
          water: Math.floor(Math.random() * 50 + 10)
        })
      }
      setConsumptionData(last7Days)

      try {
        const readingsRes = await meterReadingApi.getAll()
        setRecentReadings(readingsRes.data.slice(-10).reverse())
      } catch (e) {
        console.error('Failed to fetch readings:', e)
      }
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error)
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8']

  const alertColumns = [
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => {
        const typeMap = {
          DAILY_QUOTA_WARNING: '日配额预警',
          MONTHLY_QUOTA_EXCEED: '月配额超额',
          ABNORMAL_FLUCTUATION: '异常波动',
          DATA_MISSING: '数据缺失',
          BILL_OVERDUE: '账单逾期'
        }
        return typeMap[type] || type
      }
    },
    {
      title: '级别',
      dataIndex: 'level',
      key: 'level',
      render: (level) => {
        const color = level === 'CRITICAL' ? 'red' : level === 'WARNING' ? 'orange' : 'blue'
        const text = level === 'CRITICAL' ? '严重' : level === 'WARNING' ? '警告' : '信息'
        return <Tag color={color}>{text}</Tag>
      }
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title'
    },
    {
      title: '触发时间',
      dataIndex: 'triggeredAt',
      key: 'triggeredAt',
      render: (time) => dayjs(time).format('YYYY-MM-DD HH:mm')
    }
  ]

  const readingColumns = [
    {
      title: '表计类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => type === 'ELECTRICITY' ? (
        <Tag color="blue">电表</Tag>
      ) : (
        <Tag color="cyan">水表</Tag>
      )
    },
    {
      title: '读数',
      dataIndex: 'reading',
      key: 'reading',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '用量',
      dataIndex: 'consumption',
      key: 'consumption',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '读取时间',
      dataIndex: 'readingTime',
      key: 'readingTime',
      render: (time) => dayjs(time).format('YYYY-MM-DD HH:mm')
    }
  ]

  const keyEnterpriseCount = enterprises.filter(e => e.isKeyEnterprise).length
  const restrictedCount = enterprises.filter(e => e.servicesRestricted).length

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>仪表盘</h2>
        <Button icon={<ReloadOutlined />} onClick={fetchData} loading={loading}>
          刷新
        </Button>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={12} sm={12} md={6}>
          <Card>
            <Statistic
              title="企业总数"
              value={enterprises.length}
              prefix={<ShopOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card>
            <Statistic
              title="重点企业"
              value={keyEnterpriseCount}
              prefix={<ThunderboltOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card>
            <Statistic
              title="活跃告警"
              value={activeAlerts.length}
              prefix={<AlertOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card>
            <Statistic
              title="服务限制企业"
              value={restrictedCount}
              prefix={<DropboxOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        <Col xs={24} lg={16}>
          <Card title="最近7天能耗趋势">
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={consumptionData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="electricity" stroke="#1890ff" name="用电量 (kWh)" strokeWidth={2} />
                <Line type="monotone" dataKey="water" stroke="#13c2c2" name="用水量 (吨)" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title="企业行业分布">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={industryData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {industryData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: '16px' }}>
        <Col xs={24} lg={12}>
          <Card title="活跃告警">
            <Table
              columns={alertColumns}
              dataSource={activeAlerts}
              rowKey="id"
              size="small"
              pagination={{ pageSize: 5 }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="最近表计读数">
            <Table
              columns={readingColumns}
              dataSource={recentReadings}
              rowKey="id"
              size="small"
              pagination={{ pageSize: 5 }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard
