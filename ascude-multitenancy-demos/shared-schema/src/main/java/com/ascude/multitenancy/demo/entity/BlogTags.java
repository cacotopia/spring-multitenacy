package com.ascude.multitenancy.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ascude.multitenancy.demo.base.DataEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@TableName("blog_tags")
@Data
public class BlogTags extends DataEntity {

    /**
     * 标签名字
     */
	private String name;
    /**
     * 排序
     */
	private Integer sort;

	@TableField(exist = false)
	private Integer tagsUseCount;

	@Override
	public String toString() {
		return "BlogTags{" +
			", name=" + name +
			", sort=" + sort +
			", tagsUseCount=" + tagsUseCount +
			"}";
	}
}
