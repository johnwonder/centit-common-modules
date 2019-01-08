package com.centit.support.metadata.po;

import com.centit.framework.core.dao.DictionaryMap;
import com.centit.support.database.metadata.SimpleTableField;
import com.centit.support.database.metadata.TableField;
import com.centit.support.database.orm.GeneratorCondition;
import com.centit.support.database.orm.GeneratorTime;
import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import com.centit.support.database.utils.DBType;
import com.centit.support.metadata.utils.FieldType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;


/**
 * @author zouwy
*/
@ApiModel
@Data
@NoArgsConstructor
@Entity
@Table(name = "F_META_COLUMN")
public class MetaColumn implements TableField,java.io.Serializable {
    private static final long serialVersionUID =  201901071109L;

    @ApiModelProperty(value = "表ID", hidden = true)
    @Id
    @Column(name = "TABLE_ID")
    @NotBlank(message = "字段不能为空")
    private String tableId;

    @ApiModelProperty(value = "字段名", hidden = true)
    @Id
    @Column(name = "COLUMN_NAME")
    @NotBlank(message = "字段不能为空")
    private String  columnName;

    @ApiModelProperty(value = "字段显示名（可编辑）")
    @Column(name = "FIELD_LABEL_NAME")
    @NotBlank(message = "字段不能为空")
    @Length(max = 64, message = "字段长度不能大于{max}")
    private String  fieldLabelName;

    @ApiModelProperty(value = "字段描述（可编辑）")
    @Column(name = "COLUMN_COMMENT")
    @Length(max = 256, message = "字段长度不能大于{max}")
    private String  columnComment;

    @ApiModelProperty(value = "显示顺序（可编辑）")
    @Column(name = "COLUMN_ORDER")
    private Long columnOrder;

    @ApiModelProperty(value = "字段类型", hidden = true)
    @Column(name = "COLUMN_TYPE")
    @NotBlank(message = "字段不能为空")
    @Length(max = 32, message = "字段长度不能大于{max}")
    private String columnType;

    @ApiModelProperty(value = "字段长度", hidden = true)
    @Column(name = "MAX_LENGTH")
    private int maxLength;

    @ApiModelProperty(value = "字段精度", hidden = true)
    @Column(name = "SCALE")
    private int scale;

    /**
     * 字段类别 控制自定义表单中是否可以 访问字段
     * 比如： 最后修改时间、工作流相关的字段、更新版本号，自定义表单中就不用显示
     */
    @ApiModelProperty(value = "字段类别（可编辑）(字段类别.H：隐藏；R：只读；C：只能创建不能修改；N：可读写)")
    @Column(name = "ACCESS_TYPE")
    @NotBlank(message = "字段不能为空")
    @Length(  message = "字段长度不能大于{max}")
    private String  accessType;

    /**
     * 是否必填
     */
    @ApiModelProperty(value = "是否必填（可编辑）", hidden = true)
    @Column(name = "MANDATORY")
    @Length(  message = "字段长度不能大于{max}")
    private String  mandatory;

    /**
     * 是否为主键
     */
    @ApiModelProperty(value = "是否主键", hidden = true)
    @Column(name = "PRIMARYKEY")
    @Length(  message = "字段长度不能大于{max}")
    private String primaryKey;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态（可编辑）启用-停用")
    @Column(name = "COLUMN_STATE")
    @NotBlank(message = "字段不能为空")
    @Length(  message = "字段长度不能大于{max}")
    private String  columnState;
    /**
     *  0：没有：1： 数据字典(列表)   2： 数据字典(树型)   3：JSON表达式 4：sql语句   5：SQL（树）
       9 :框架内置字典（用户、机构、角色等等）  Y：年份 M：月份   F:文件（column_Type 必须为 varchar（64））
     */
    @ApiModelProperty(value = "引用数据类型（可编辑）（0：没有 1：数据字典(列表) 2：数据字典(树型) 3：JSON表达式 4：sql语句 5：SQL（树）Y：年份 M：月份 )")
    @Column(name = "REFERENCE_TYPE")
    @Length(  message = "字段长度不能大于{max}")
    private String  referenceType;
    /**
     * 引用数据 根据paramReferenceType类型（1,2,3）填写对应值
     */
    @ApiModelProperty(value = "引用数据（可编辑）")
    @Column(name = "REFERENCE_DATA")
    @Length(max = 1000, message = "字段长度不能大于{max}")
    private String  referenceData;
    /**
     * 约束表达式 regex表达式
     */
    @ApiModelProperty(value = "约束表达式 regex表达式（可编辑）")
    @Column(name = "VALIDATE_REGEX")
    @Length(max = 200, message = "字段长度不能大于{max}")
    private String  validateRegex;
    /**
     * 约束提示 约束不通过提示信息
     */
    @ApiModelProperty(value = "约束提示 约束不通过提示信息（可编辑）")
    @Column(name = "VALIDATE_INFO")
    @Length(max = 200, message = "字段长度不能大于{max}")
    private String  validateInfo;

    /**
     * 自动生成规则   C 常量 F 表达式  U uuid S sequence
     */
    @ApiModelProperty(value = "自动生成规则（可编辑）   C：常量 F：表达式  U：uuid S：sequence")
    @Column(name = "AUTO_CREATE_RULE")
    @Length(max = 200, message = "字段长度不能大于{max}")
    private String  autoCreateRule;

    /**
     * 自动生成参数 常量的值、表达式 、 或者sequence的名字
     */
    @ApiModelProperty(value = "自动生成参数 常量的值、表达式、或者sequence的名字（可编辑）")
    @Column(name = "AUTO_CREATE_PARAM")
    @Length(max = 200, message = "字段长度不能大于{max}")
    private String  autoCreateParam;
    /**
     * 更改时间
     */
    @ApiModelProperty(hidden = true)
    @Column(name = "LAST_MODIFY_DATE")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, occasion = GeneratorTime.NEW_UPDATE, condition = GeneratorCondition.ALWAYS, value = "today()")
    private Date  lastModifyDate;
    /**
     * 更改人员 null
     */
    @ApiModelProperty(value = "更改人员", hidden = true)
    @Column(name = "RECORDER")
    @Length(max = 8, message = "字段长度不能大于{max}")
    @DictionaryMap(fieldName = "recorderName", value = "userCode")
    private String  recorder;

    @Transient
    @ApiModelProperty(hidden = true)
    private DBType databaseType;

    public MetaColumn(@NotBlank(message = "字段不能为空") String tableId, @NotBlank(message = "字段不能为空") String columnName) {
        this.tableId = tableId;
        this.columnName = columnName;
    }

    public MetaColumn convertFromTableField(SimpleTableField tableField){
        this.columnName = tableField.getColumnName();
        this.columnType = tableField.getColumnType();
        if(StringUtils.isNotBlank(tableField.getFieldLabelName())){
            this.fieldLabelName = tableField.getFieldLabelName();
        }
        if(StringUtils.isNotBlank(tableField.getColumnComment()) && StringUtils.isBlank(this.columnComment)){
            this.columnComment = tableField.getColumnComment();
        }
        this.maxLength = tableField.getMaxLength();
        this.scale = tableField.getScale();
        this.mandatory = tableField.isMandatory() ? "T" : "F";
        return this;
    }

    /**
     * 这个是用于生产数据库表创建语句的，不是用来生成表单默认值的
     */
    public String getDefaultValue() {
        return "C".equals(autoCreateRule)?autoCreateParam:null;
        /*switch(autoCreateRule){
        case "C":
            return autoCreateParam;
        case "U":
            return UuidOpt.getUuidAsString();
        case "S":
            return null;
        default:
            return null;
        }*/
    }


    @ApiModelProperty(hidden = true)
    @Override
    public String getPropertyName() {
        return SimpleTableField.mapPropName(getColumnName());
    }

    public static String mapToFieldType(String columnType,int scale)
    {
        if("NUMBER".equalsIgnoreCase(columnType) ||
           "INTEGER".equalsIgnoreCase(columnType)||
           "DECIMAL".equalsIgnoreCase(columnType) ){
            if( scale > 0 )
                return FieldType.FLOAT;
            else
                return FieldType.INTEGER;
        }else if("FLOAT".equalsIgnoreCase(columnType)){
            return FieldType.FLOAT;
        }else if("CHAR".equalsIgnoreCase(columnType) ||
               "VARCHAR".equalsIgnoreCase(columnType)||
               "VARCHAR2".equalsIgnoreCase(columnType)||
               "STRING".equalsIgnoreCase(columnType) ){
            return FieldType.STRING;
        }else if("DATE".equalsIgnoreCase(columnType) ||
                   "TIME".equalsIgnoreCase(columnType) ){
            return FieldType.DATE;
        }else if("TIMESTAMP".equalsIgnoreCase(columnType)||
                   "DATETIME".equalsIgnoreCase(columnType) ){
            return FieldType.DATETIME;
        }else if("CLOB".equalsIgnoreCase(columnType) /*||
                   "LOB".equalsIgnoreCase(sDBType)||
                   "BLOB".equalsIgnoreCase(sDBType)*/ ){
            return FieldType.TEXT;
        }else if("BOOLEAN".equalsIgnoreCase(columnType) ){
            return FieldType.BOOLEAN;
        }else
            return columnType;
    }

    @Override
    public String getJavaType() {
        return MetaColumn.mapToFieldType(this.columnType,this.scale);
    }

    @Override
    public boolean isMandatory() {
        return "T".equals(mandatory) ||  "Y".equals(mandatory) || "1".equals(mandatory);
    }

    public boolean isPrimaryKey() {
        return "T".equals(primaryKey) ||  "Y".equals(primaryKey) || "1".equals(primaryKey);
    }

    @Override
    public int getMaxLength() {
        if("string".equalsIgnoreCase(this.columnType) ||
                "integer".equalsIgnoreCase(this.columnType)||
                "float".equalsIgnoreCase(this.columnType) ||
                "varchar".equalsIgnoreCase(this.columnType)||
                "number".equalsIgnoreCase(this.columnType))
            return maxLength;
        return 0;
    }

    public void setMaxLength(int maxLength){
        this.maxLength =  maxLength;
    }


    @Override
    public int getPrecision() {
        return getMaxLength();
    }
    @Override
    public int getScale() {
        if("float".equalsIgnoreCase(this.columnType) ||
                "number".equalsIgnoreCase(this.columnType))
            return scale;
        return 0;
    }

    public void setScale(int scale){
        this.scale = scale;
    }

    @Override
    public String getColumnType() {
        return FieldType.mapToDBColumnType(this.databaseType, this.columnType);
    }
}
