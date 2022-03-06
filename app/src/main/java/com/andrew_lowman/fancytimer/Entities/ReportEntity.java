package com.andrew_lowman.fancytimer.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "reports_table",
        foreignKeys = {@ForeignKey(entity = IntervalsEntity.class, parentColumns = "intervalID",
        childColumns = "fkIntervalID")},indices = {@Index(value = {"fkIntervalID"},unique = true)})
public class ReportEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reportID")
    private int reportID;

    @ColumnInfo(name = "fkIntervalID")
    private int fkIntervalID;

    @ColumnInfo(name = "intervalName")
    private String intervalName;

    @ColumnInfo(name = "reportCode")
    private String reportCode;

    @ColumnInfo(name = "numberOfTimesRun")
    private int numberOfTimesRun;

    public ReportEntity(int fkIntervalID, String intervalName, String reportCode, int numberOfTimesRun) {
        this.fkIntervalID = fkIntervalID;
        this.intervalName = intervalName;
        this.reportCode = reportCode;
        this.numberOfTimesRun = numberOfTimesRun;
    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public int getFkIntervalID() {
        return fkIntervalID;
    }

    public void setFkIntervalID(int fkIntervalID) {
        this.fkIntervalID = fkIntervalID;
    }

    public String getIntervalName() {
        return intervalName;
    }

    public void setIntervalName(String intervalName) {
        this.intervalName = intervalName;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public void addToReportCode(String code){
        this.reportCode += code;
    }

    public int getNumberOfTimesRun() {
        return numberOfTimesRun;
    }

    public void setNumberOfTimesRun(int numberOfTimesRun) {
        this.numberOfTimesRun = numberOfTimesRun;
    }

    public void addToNumberOfTimesRun(){
        this.numberOfTimesRun++;
    }

    public void subtractFromNumberOfTimesRun(){
        this.numberOfTimesRun--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportEntity that = (ReportEntity) o;
        return fkIntervalID == that.fkIntervalID;
    }

}
