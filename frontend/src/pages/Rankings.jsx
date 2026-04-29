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
  Tabs
} from 'antd'
import { ReloadOutlined, TrophyOutlined, ThunderboltOutlined, DropboxOutlined, RiseOutlined } from '@ant-design/icons'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import dayjs from 'dayjs'
import { rankingApi, enterpriseApi } from '../api'

const { TabPane } = Tabs

const Rankings = () => {
  const [rankings, setRankings] = useState([])
  const [enterprises, setEnterprises] = useState([])
  const [loading, setLoading] = useState(false)
  const [selectedYear, setSelectedYear] = useState(dayjs().year())
  const [selectedMonth, setSelectedMonth] = useState(dayjs().month() + 1)

  useEffect(() => {
    fetchData()
  }, [selectedYear, selectedMonth])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [rankingsRes, enterprisesRes] = await Promise.all([
        rankingApi.getByMonth(selectedYear, selectedMonth),
        enterpriseApi.getAll()
      ])

      setRankings(rankingsRes.data)
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

  const getRankColor = (rank) => {
    if (rank === 1) return '#faad14'
    if (rank === 2) return '#c0c0c0'
    if (rank === 3) return '#cd7f32'
    return '#666'
  }

  const electricityRankings = rankings
    .filter(r => r.type === 'ELECTRICITY_CONSUMPTION')
    .sort((a, b) => a.rank - b.rank)

  const waterRankings = rankings
    .filter(r => r.type === 'WATER_CONSUMPTION')
    .sort((a, b) => a.rank - b.rank)

  const totalRankings = rankings
    .filter(r => r.type === 'TOTAL_CONSUMPTION')
    .sort((a, b) => a.rank - b.rank)

  const columns = [
    {
      title: '排名',
      dataIndex: 'rank',
      key: 'rank',
      width: 80,
      render: (rank) => {
        const icon = rank <= 3 ? <TrophyOutlined style={{ color: getRankColor(rank), fontSize: '18px' }} /> : rank
        return (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            {icon}
          </div>
        )
      }
    },
    {
      title: '企业名称',
      dataIndex: 'enterpriseId',
      key: 'enterpriseId',
      render: (enterpriseId) => getEnterpriseName(enterpriseId)
    },
    {
      title: '能耗值',
      dataIndex: 'value',
      key: 'value',
      render: (val) => <span style={{ fontWeight: 'bold', fontSize: '16px' }}>{val?.toFixed(2)}</span>
    }
  ]

  const chartData = totalRankings.slice(0, 10).map((r, index) => ({
    name: getEnterpriseName(r.enterpriseId),
    value: r.value,
    rank: r.rank
  }))

  const generateRankings = async () => {
    try {
      await rankingApi.generate(selectedYear, selectedMonth)
      message.success('排名已生成')
      fetchData()
    } catch (error) {
      console.error('Failed to generate rankings:', error)
      message.error('生成排名失败')
    }
  }

  const years = []
  const currentYear = dayjs().year()
  for (let i = currentYear; i >= currentYear - 3; i--) {
    years.push(i)
  }

  const months = Array.from({ length: 12 }, (_, i) => i + 1)

  const top3Electricity = electricityRankings.slice(0, 3)
  const top3Water = waterRankings.slice(0, 3)
  const top3Total = totalRankings.slice(0, 3)

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>能耗排名</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button type="primary" icon={<RiseOutlined />} onClick={generateRankings}>
            生成排名
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
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="用电最少"
              value={top3Electricity.length > 0 ? getEnterpriseName(top3Electricity[0].enterpriseId) : '-'}
              prefix={<TrophyOutlined style={{ color: '#faad14' }} />}
              valueStyle={{ color: '#3f8600', fontSize: '14px' }}
            />
            <div style={{ marginTop: '8px', color: '#666', fontSize: '12px' }}>
              {top3Electricity.length > 0 ? `${top3Electricity[0].value.toFixed(2)} kWh` : ''}
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="用水最少"
              value={top3Water.length > 0 ? getEnterpriseName(top3Water[0].enterpriseId) : '-'}
              prefix={<TrophyOutlined style={{ color: '#faad14' }} />}
              valueStyle={{ color: '#3f8600', fontSize: '14px' }}
            />
            <div style={{ marginTop: '8px', color: '#666', fontSize: '12px' }}>
              {top3Water.length > 0 ? `${top3Water[0].value.toFixed(2)} 吨` : ''}
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="综合最少"
              value={top3Total.length > 0 ? getEnterpriseName(top3Total[0].enterpriseId) : '-'}
              prefix={<TrophyOutlined style={{ color: '#faad14' }} />}
              valueStyle={{ color: '#3f8600', fontSize: '14px' }}
            />
            <div style={{ marginTop: '8px', color: '#666', fontSize: '12px' }}>
              {top3Total.length > 0 ? `排名第 ${top3Total[0].rank}` : ''}
            </div>
          </Card>
        </Col>
      </Row>

      {chartData.length > 0 && (
        <Card title="能耗排名图表 (前10名)" style={{ marginBottom: '16px' }}>
          <ResponsiveContainer width="100%" height={350}>
            <BarChart data={chartData} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis type="number" />
              <YAxis dataKey="name" type="category" width={120} />
              <Tooltip />
              <Legend />
              <Bar dataKey="value" fill="#1890ff" name="综合能耗" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </Card>
      )}

      <Card>
        <Tabs defaultActiveKey="total">
          <TabPane tab={<span><TrophyOutlined /> 综合排名</span>} key="total">
            <Table
              columns={columns}
              dataSource={totalRankings}
              rowKey="id"
              loading={loading}
              pagination={false}
            />
          </TabPane>
          <TabPane tab={<span><ThunderboltOutlined /> 用电排名</span>} key="electricity">
            <Table
              columns={columns}
              dataSource={electricityRankings}
              rowKey="id"
              loading={loading}
              pagination={false}
            />
          </TabPane>
          <TabPane tab={<span><DropboxOutlined /> 用水排名</span>} key="water">
            <Table
              columns={columns}
              dataSource={waterRankings}
              rowKey="id"
              loading={loading}
              pagination={false}
            />
          </TabPane>
        </Tabs>
      </Card>
    </div>
  )
}

export default Rankings
