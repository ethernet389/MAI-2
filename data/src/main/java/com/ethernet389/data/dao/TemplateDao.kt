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

    @Insert
    fun insertTemplates(dataTemplates: List<DataTemplate>): List<Long>

    @Query("SELECT * FROM templates;")
    fun getAllTemplates(): List<DataTemplate>

    @Delete
    fun deleteTemplate(template: DataTemplate): Int

    @Query("SELECT * FROM templates WHERE _id = :id;")
    fun getTemplateById(id: Long): DataTemplate

    @Query(
        """
        DELETE FROM templates
        WHERE
            (SELECT COUNT(notes.template_id)
            FROM notes
            WHERE templates._id = notes.template_id) = 0;
        """
    )
    fun deleteUnusedTemplates()
}