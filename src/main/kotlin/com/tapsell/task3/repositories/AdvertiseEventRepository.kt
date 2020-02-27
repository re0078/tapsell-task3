package com.tapsell.task3.repositories

import com.tapsell.task3.entities.AdvertiseEvent
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface AdvertiseEventRepository : CassandraRepository<AdvertiseEvent, String>