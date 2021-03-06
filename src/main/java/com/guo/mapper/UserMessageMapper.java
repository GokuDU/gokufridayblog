package com.guo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.UserMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guo.vo.UserMessageVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Component
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    IPage<UserMessageVO> selectMessages(Page page,@Param(Constants.WRAPPER) QueryWrapper<UserMessage> wrapper);

    @Transactional
    @Update("update user_message set status = 1 ${ew.customSqlSegment}")
    void updateStatusToReaded(@Param(Constants.WRAPPER) QueryWrapper<UserMessage> wrapper);
}
