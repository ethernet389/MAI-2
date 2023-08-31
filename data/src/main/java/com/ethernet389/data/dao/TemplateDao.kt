package com.ethernet389.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ethernet389.data.model.DataTemplate

@Dao
interface TemplateDao {
    @Insert
    fun insertTemplate(dataTemplate: DataTemplate): Long

    @Query("SELECT * FROM templates")
    fun getAllTemplates(): List<DataTemplate>

    @Delete
    fun deleteTemplate(template: DataTemplate): Long

    @Query("SELECT * FROM templates WHERE id = :id")
    fun getTemplateById(id: Long): DataTemplate
}