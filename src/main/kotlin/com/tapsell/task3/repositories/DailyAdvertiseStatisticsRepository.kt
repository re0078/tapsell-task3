package com.tapsell.task3.repositories

import com.tapsell.task3.entities.DailyAdvertiseStatistics
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface DailyAdvertiseStatisticsRepository : CassandraRepository<DailyAdvertiseStatistics, String>