package com.tapsell.task3.repositories

import com.tapsell.task3.entities.CTRStatRecord
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CTRStatRepo : CrudRepository<CTRStatRecord, String>