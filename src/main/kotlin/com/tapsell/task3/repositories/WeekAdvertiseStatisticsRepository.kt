package com.tapsell.task3.repositories

import com.tapsell.task3.entities.WeekAdvertiseStatistics
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface WeekAdvertiseStatisticsRepository : CassandraRepository<WeekAdvertiseStatistics, String>