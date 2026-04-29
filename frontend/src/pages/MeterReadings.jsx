import React, { useState, useEffect } from 'react'
import {
  Card,
  Table,
  Tag,
  Space,
  Button,
  Select,
  DatePicker,
  Row,
  Col,
  message
} from 'antd'
import { ReloadOutlined, ThunderboltOutlined, DropboxOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { meterReadingApi, meterApi, statisticsApi, enterpriseApi } from '../api'

const { RangePicker } = DatePicker

const MeterReadings = () => {
  const [readings, setReadings] = useState([])
  const [meters, setMeters] = useState([])
  const [enterprises, setEnterprises] = useState([])
  const [statistics, setStatistics] = useState([])
  const [loading, setLoading] = useState(false)
  const [selectedEnterprise, setSelectedEnterprise] = useState(null)
  const [selectedType, setSelectedType] = useState(null)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [readingsRes, metersRes, enterprisesRes, statsRes] = await Promise.all([
        meterReadingApi.getAll(),
        meterApi.getAll(),
        enterpriseApi.getAll(),
        statisticsApi.getAll()
      ])

      setReadings(readingsRes.data)
      setMeters(metersRes.data)
      setEnterprises(enterprisesRes.data)
      setStatistics(statsRes.data)
    } catch (error) {
      console.error('Failed to fetch data:', error)
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const getMeterName = (meterId) => {
    const meter = meters.find(m => m.id === meterId)
    return meter ? meter.name : meterId
  }

  const getEnterpriseName = (enterpriseId) => {
    const enterprise = enterprises.find(e => e.id === enterpriseId)
    return enterprise ? enterprise.name : enterpriseId
  }

  const readingsColumns = [
    {
      title: '表计名称',
      dataIndex: 'meterId',
      key: 'meterId',
      render: (meterId) => getMeterName(meterId)
    },
    {
      title: '企业',
      dataIndex: 'enterpriseId',
      key: 'enterpriseId',
      render: (enterpriseId) => getEnterpriseName(enterpriseId)
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => type === 'ELECTRICITY' ? (
        <Tag icon={<ThunderboltOutlined />} color="blue">电表</Tag>
      ) : (
        <Tag icon={<DropboxOutlined />} color="cyan">水表</Tag>
      )
    },
    {
      title: '当前读数',
      dataIndex: 'reading',
      key: 'reading',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '本次用量',
      dataIndex: 'consumption',
      key: 'consumption',
      render: (val) => <span style={{ fontWeight: 'bold', color: '#1890ff' }}>{val?.toFixed(2)}</span>
    },
    {
      title: '读取时间',
      dataIndex: 'readingTime',
      key: 'readingTime',
      render: (time) => dayjs(time).format('YYYY-MM-DD HH:mm:ss')
    }
  ]

  const statsColumns = [
    {
      title: '企业',
      dataIndex: 'enterpriseId',
      key: 'enterpriseId',
      render: (enterpriseId) => getEnterpriseName(enterpriseId)
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => type === 'ELECTRICITY' ? (
        <Tag color="blue">电表</Tag>
      ) : (
        <Tag color="cyan">水表</Tag>
      )
    },
    {
      title: '统计周期',
      dataIndex: 'period',
      key: 'period',
      render: (period) => {
        const periodMap = {
          HOURLY: '小时',
          DAILY: '天',
          MONTHLY: '月'
        }
        return <Tag>{periodMap[period] || period}</Tag>
      }
    },
    {
      title: '周期日期',
      dataIndex: 'periodDate',
      key: 'periodDate',
      render: (date) => dayjs(date).format('YYYY-MM-DD')
    },
    {
      title: '总用量',
      dataIndex: 'consumption',
      key: 'consumption',
      render: (val) => <span style={{ fontWeight: 'bold' }}>{val?.toFixed(2)}</span>
    },
    {
      title: '配额',
      dataIndex: 'quota',
      key: 'quota',
      render: (val) => val?.toFixed(2)
    },
    {
      title: '配额使用',
      dataIndex: 'quotaPercentage',
      key: 'quotaPercentage',
      render: (val) => {
        if (!val) return <Tag color="default">0%</Tag>
        const color = val > 100 ? 'red' : val > 90 ? 'orange' : 'green'
        return <Tag color={color}>{val.toFixed(1)}%</Tag>
      }
    }
  ]

  const filteredReadings = readings.filter(r => {
    let match = true
    if (selectedEnterprise) {
      const meter = meters.find(m => m.id === r.meterId)
      match = match && meter?.enterpriseId === selectedEnterprise
    }
    if (selectedType) {
      match = match && r.type === selectedType
    }
    return match
  })

  const filteredStats = statistics.filter(s => {
    let match = true
    if (selectedEnterprise) {
      match = match && s.enterpriseId === selectedEnterprise
    }
    if (selectedType) {
      match = match && s.type === selectedType
    }
    return match
  })

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2>用量数据</h2>
        <Button icon={<ReloadOutlined />} onClick={fetchData} loading={loading}>
          刷新
        </Button>
      </div>

      <Card style={{ marginBottom: '16px' }}>
        <Row gutter={16}>
          <Col xs={24} sm={12} md={8}>
            <div style={{ marginBottom: '8px', fontWeight: 'bold' }}>企业筛选</div>
            <Select
              placeholder="选择企业"
              style={{ width: '100%' }}
              allowClear
              onChange={setSelectedEnterprise}
              value={selectedEnterprise}
            >
              {enterprises.map(e => (
                <Select.Option key={e.id} value={e.id}>
                  {e.name} {e.isKeyEnterprise ? '(重点企业)' : ''}
                </Select.Option>
              ))}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8}>
            <div style={{ marginBottom: '8px', fontWeight: 'bold' }}>类型筛选</div>
            <Select
              placeholder="选择类型"
              style={{ width: '100%' }}
              allowClear
              onChange={setSelectedType}
              value={selectedType}
            >
              <Select.Option value="ELECTRICITY">电表</Select.Option>
              <Select.Option value="WATER">水表</Select.Option>
            </Select>
          </Col>
        </Row>
      </Card>

      <Card title="用量统计" style={{ marginBottom: '16px' }}>
        <Table
          columns={statsColumns}
          dataSource={filteredStats}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>

      <Card title="表计读数记录">
        <Table
          columns={readingsColumns}
          dataSource={filteredReadings}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条记录`
          }}
        />
      </Card>
    </div>
  )
}

export default MeterReadings
