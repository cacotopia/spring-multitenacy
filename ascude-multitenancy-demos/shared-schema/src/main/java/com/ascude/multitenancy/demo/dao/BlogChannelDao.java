package com.ascude.multitenancy.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ascude.multitenancy.demo.entity.BlogChannel;
import com.ascude.multitenancy.demo.entity.VO.BlogChannelVO;
import com.ascude.multitenancy.demo.entity.VO.ZtreeVO;

import java.util.List;
import java.util.Map;

public interface BlogChannelDao extends BaseMapper<BlogChannel> {

    List<ZtreeVO> selectZtreeData(Map<String,Object> map);

    List<BlogChannelVO> selectChannelData(Map<String, Object> map);
}
