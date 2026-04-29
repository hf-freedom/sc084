import React, { useState, useEffect } from 'react'
import {
  Table,
  Card,
  Button,
  Tag,
  Space,
  message,
  Badge,
  Popconfirm,
  Tabs,
  Dropdown
} from 'antd'
import {
  ReloadOutlined,
  CheckCircleOutlined,
  StopOutlined,
  BellOutlined,
  DownOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { alertApi } from '../api'

const Alerts = () => {
  const [alerts, setAlerts] = useState([])
  const [loading, setLoading] = useState(false)
  const [generating, setGenerating] = useState(false)
  const [activeKey, setActiveKey] = useState('all')

  useEffect(() => {
    fetchAlerts()
  }, [])

  const fetchAlerts = async () => {
    setLoading(true)
    try {
      const response = await alertApi.getAll()
      setAlerts(response.data)
    } catch (error) {
      console.error('Failed to fetch alerts:', error)
      message.error('加载告警列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleResolve = async (id) => {
    try {
      await alertApi.resolve(id, 'admin')
      message.success('告警已解决')
      fetchAlerts()
    } catch (error) {
      console.error('Failed to resolve alert:', error)
      message.error('解决告警失败')
    }
  }

  const handleIgnore = async (id) => {
    try {
      await alertApi.ignore(id, 'admin')
      message.success('告警已忽略')
      fetchAlerts()
    } catch (error) {
      console.error('Failed to ignore alert:', error)
      message.error('忽略告警失败')
    }
  }

  const handleGenerateDailyQuotas = async () => {
    setGenerating(true)
    try {
      const response = await alertApi.generateDailyQuotas()
      const count = response.data.length
      if (count > 0) {
        message.success(`成功生成 ${count} 条日配额告警`)
      } else {
        message.info('没有企业超过日配额阈值(90%)，未生成新告警')
      }
      fetchAlerts()
    } catch (error) {
      console.error('Failed to generate daily alerts:', error)
      message.error('生成日配额告警失败')
    } finally {
      setGenerating(false)
    }
  }

  const handleGenerateMonthlyQuotas = async () => {
    setGenerating(true)
    try {
      const response = await alertApi.generateMonthlyQuotas()
      const count = response.data.length
      if (count > 0) {
        message.success(`成功生成 ${count} 条月配额告警`)
      } else {
        message.info('没有企业超过月配额(100%)，未生成新告警')
      }
      fetchAlerts()
    } catch (error) {
      console.error('Failed to generate monthly alerts:', error)
      message.error('生成月配额告警失败')
    } finally {
      setGenerating(false)
    }
  }

  const handleGenerateAllQuotas = async () => {
    setGenerating(true)
    try {
      const response = await alertApi.generateAllQuotas()
      const count = response.data.length
      if (count > 0) {
        message.success(`成功生成 ${count} 条配额告警`)
      } else {
        message.info('没有企业超过配额阈值，未生成新告警')
      }
      fetchAlerts()
    } catch (error) {
      console.error('Failed to generate alerts:', error)
      message.error('生成告警失败')
    } finally {
      setGenerating(false)
    }
  }

  const getTypeText = (type) => {
    const typeMap = {
      DAILY_QUOTA_WARNING: '日配额预警',
      MONTHLY_QUOTA_EXCEED: '月配额超额',
      ABNORMAL_FLUCTUATION: '异常波动',
      DATA_MISSING: '数据缺失',
      BILL_OVERDUE: '账单逾期'
    }
    return typeMap[type] || type
  }

  const getLevelColor = (level) => {
    const colorMap = {
      CRITICAL: 'red',
      WARNING: 'orange',
      INFO: 'blue'
    }
    return colorMap[level] || 'default'
  }

  const getLevelText = (level) => {
    const textMap = {
      CRITICAL: '严重',
      WARNING: '警告',
      INFO: '信息'
    }
    return textMap[level] || level
  }

  const getStatusColor = (status) => {
    const colorMap = {
      ACTIVE: 'processing',
      RESOLVED: 'success',
      IGNORED: 'default'
    }
    return colorMap[status] || 'default'
  }

  const getStatusText = (status) => {
    const textMap = {
      ACTIVE: '活跃',
      RESOLVED: '已解决',
      IGNORED: '已忽略'
    }
    return textMap[status] || status
  }

  const columns = [
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type) => <Tag color="blue">{getTypeText(type)}</Tag>,
      filters: [
        { text: '日配额预警', value: 'DAILY_QUOTA_WARNING' },
        { text: '月配额超额', value: 'MONTHLY_QUOTA_EXCEED' },
        { text: '异常波动', value: 'ABNORMAL_FLUCTUATION' },
        { text: '数据缺失', value: 'DATA_MISSING' },
        { text: '账单逾期', value: 'BILL_OVERDUE' }
      ],
      onFilter: (value, record) => record.type === value
    },
    {
      title: '级别',
      dataIndex: 'level',
      key: 'level',
      render: (level) => <Tag color={getLevelColor(level)}>{getLevelText(level)}</Tag>,
      filters: [
        { text: '严重', value: 'CRITICAL' },
        { text: '警告', value: 'WARNING' },
        { text: '信息', value: 'INFO' }
      ],
      onFilter: (value, record) => record.level === value
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title'
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Badge status={getStatusColor(status)} text={getStatusText(status)} />
      ),
      filters: [
        { text: '活跃', value: 'ACTIVE' },
        { text: '已解决', value: 'RESOLVED' },
        { text: '已忽略', value: 'IGNORED' }
      ],
      onFilter: (value, record) => record.status === value
    },
    {
      title: '触发时间',
      dataIndex: 'triggeredAt',
      key: 'triggeredAt',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-',
      sorter: (a, b) => dayjs(a.triggeredAt).unix() - dayjs(b.triggeredAt).unix()
    },
    {
      title: '解决时间',
      dataIndex: 'resolvedAt',
      key: 'resolvedAt',
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'ACTIVE' && (
            <>
              <Popconfirm
                title="确定要解决该告警吗？"
                onConfirm={() => handleResolve(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" icon={<CheckCircleOutlined />}>
                  解决
                </Button>
              </Popconfirm>
              <Popconfirm
                title="确定要忽略该告警吗？"
                onConfirm={() => handleIgnore(record.id)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" icon={<StopOutlined />} danger>
                  忽略
                </Button>
              </Popconfirm>
            </>
          )}
        </Space>
      )
    }
  ]

  const tabItems = [
    {
      key: 'all',
      label: '全部告警'
    },
    {
      key: 'active',
      label: (
        <span>
          活跃告警
          <Badge
            count={alerts.filter(a => a.status === 'ACTIVE').length}
            style={{ marginLeft: 8 }}
          />
        </span>
      )
    }
  ]

  const filteredAlerts = activeKey === 'active'
    ? alerts.filter(a => a.status === 'ACTIVE')
    : alerts

  const dropdownMenu = {
    items: [
      {
        key: 'daily',
        label: '生成日配额告警 (日用量≥90%)',
        onClick: handleGenerateDailyQuotas
      },
      {
        key: 'monthly',
        label: '生成月配额告警 (月用量>100%)',
        onClick: handleGenerateMonthlyQuotas
      },
      {
        key: 'all',
        label: '生成所有配额告警',
        onClick: handleGenerateAllQuotas
      }
    ]
  }

  return (
    <div>
      <Card
        title="告警中心"
        extra={
          <Space>
            <Dropdown.Button
              menu={dropdownMenu}
              icon={<DownOutlined />}
              type="primary"
              onClick={handleGenerateAllQuotas}
              loading={generating}
            >
              <BellOutlined /> 生成配额告警
            </Dropdown.Button>
            <Button icon={<ReloadOutlined />} onClick={fetchAlerts} loading={loading}>
              刷新
            </Button>
          </Space>
        }
      >
        <Tabs
          activeKey={activeKey}
          onChange={setActiveKey}
          items={tabItems}
        />
        <Table
          columns={columns}
          dataSource={filteredAlerts}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </div>
  )
}

export default Alerts
