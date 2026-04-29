import React, { useState, useEffect } from 'react'
import { Layout, Menu, Badge, Dropdown, Avatar } from 'antd'
import {
  DashboardOutlined,
  ShopOutlined,
  ThunderboltOutlined,
  FileTextOutlined,
  AlertOutlined,
  TrophyOutlined,
  CarryOutOutlined,
  ReloadOutlined,
  UserOutlined,
  ToolOutlined
} from '@ant-design/icons'
import { Routes, Route, Link, useNavigate, useLocation } from 'react-router-dom'
import Dashboard from './pages/Dashboard'
import Enterprises from './pages/Enterprises'
import MeterReadings from './pages/MeterReadings'
import Bills from './pages/Bills'
import Alerts from './pages/Alerts'
import Rankings from './pages/Rankings'
import Assessments from './pages/Assessments'
import InspectionTasks from './pages/InspectionTasks'
import ResampleTasks from './pages/ResampleTasks'
import { alertApi } from './api'

const { Header, Sider, Content } = Layout

const App = () => {
  const [collapsed, setCollapsed] = useState(false)
  const [activeAlerts, setActiveAlerts] = useState([])
  const location = useLocation()
  const navigate = useNavigate()

  useEffect(() => {
    fetchActiveAlerts()
    const interval = setInterval(fetchActiveAlerts, 30000)
    return () => clearInterval(interval)
  }, [])

  const fetchActiveAlerts = async () => {
    try {
      const response = await alertApi.getActive()
      setActiveAlerts(response.data)
    } catch (error) {
      console.error('Failed to fetch active alerts:', error)
    }
  }

  const menuItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: <Link to="/">仪表盘</Link>
    },
    {
      key: '/enterprises',
      icon: <ShopOutlined />,
      label: <Link to="/enterprises">企业管理</Link>
    },
    {
      key: '/meter-readings',
      icon: <ThunderboltOutlined />,
      label: <Link to="/meter-readings">用量数据</Link>
    },
    {
      key: '/bills',
      icon: <FileTextOutlined />,
      label: <Link to="/bills">账单管理</Link>
    },
    {
      key: '/alerts',
      icon: <AlertOutlined />,
      label: <Link to="/alerts">告警中心</Link>,
      badge: activeAlerts.length > 0 ? { count: activeAlerts.length, color: '#ff4d4f' } : null
    },
    {
      key: '/rankings',
      icon: <TrophyOutlined />,
      label: <Link to="/rankings">能耗排名</Link>
    },
    {
      key: '/assessments',
      icon: <CarryOutOutlined />,
      label: <Link to="/assessments">节能考核</Link>
    },
    {
      key: '/inspection-tasks',
      icon: <ToolOutlined />,
      label: <Link to="/inspection-tasks">巡检任务</Link>
    },
    {
      key: '/resample-tasks',
      icon: <ReloadOutlined />,
      label: <Link to="/resample-tasks">补采任务</Link>
    }
  ]

  const userMenu = [
    {
      key: '1',
      label: '个人信息'
    },
    {
      key: '2',
      label: '系统设置'
    },
    {
      key: '3',
      label: '退出登录'
    }
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div className="logo">
          {collapsed ? '能耗' : '园区能耗管理系统'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems.map(item => ({
            ...item,
            label: item.badge ? (
              <Badge {...item.badge}>
                {item.label}
              </Badge>
            ) : item.label
          }))}
        />
      </Sider>
      <Layout>
        <Header style={{ background: '#fff', padding: '0 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ fontSize: '18px', fontWeight: 'bold' }}>
            园区能耗配额与管理系统
          </div>
          <Dropdown menu={{ items: userMenu }} placement="bottomRight">
            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Avatar icon={<UserOutlined />} />
              <span>管理员</span>
            </div>
          </Dropdown>
        </Header>
        <Content style={{ margin: '16px', background: '#f0f2f5' }}>
          <div style={{ padding: 24, minHeight: 360, background: '#fff', borderRadius: '8px' }}>
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/enterprises" element={<Enterprises />} />
              <Route path="/meter-readings" element={<MeterReadings />} />
              <Route path="/bills" element={<Bills />} />
              <Route path="/alerts" element={<Alerts />} />
              <Route path="/rankings" element={<Rankings />} />
              <Route path="/assessments" element={<Assessments />} />
              <Route path="/inspection-tasks" element={<InspectionTasks />} />
              <Route path="/resample-tasks" element={<ResampleTasks />} />
            </Routes>
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}

export default App
