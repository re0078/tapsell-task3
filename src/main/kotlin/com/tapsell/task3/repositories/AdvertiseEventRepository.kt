package com.tapsell.task3.repositories

import com.tapsell.task3.entities.AdvertiseEvent
import org.springframework.data.cassandra.repository.CassandraRepository

interface AdvertiseEventRepository : CassandraRepository<AdvertiseEvent, String>