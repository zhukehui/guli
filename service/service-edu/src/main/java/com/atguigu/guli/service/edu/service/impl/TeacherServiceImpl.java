package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.atguigu.guli.service.edu.mapper.TeacherMapper;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author eternity
 * @since 2019-11-20
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public IPage<Teacher> selectPage(Page<Teacher> pageParam, TeacherQueryVo teacherQueryVo) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");

        if (teacherQueryVo == null) {//如果为null，则只按照排序分页查询
            return baseMapper.selectPage(pageParam, queryWrapper);
        }

        //获取查询条件
        String name = teacherQueryVo.getName();//讲师姓名
        Integer level = teacherQueryVo.getLevel();//讲师级别
        String begin = teacherQueryVo.getJoinDateBegin();//讲师入驻的开始时间
        String end = teacherQueryVo.getJoinDateEnd();//讲师讲师入驻的结束时间

        //确保条件都不为空

        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name", name);
        }

        if (level != null) {
            queryWrapper.eq("level", level);//eq等于
        }

        if (!StringUtils.isEmpty(begin)) {
            queryWrapper.ge("join_date", begin);//ge 大于等于
        }

        if (!StringUtils.isEmpty(end)) {
            queryWrapper.le("join_date", end);//le 小于等于
        }

        return baseMapper.selectPage(pageParam, queryWrapper);
    }



 /*   @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id){//删除失败或者不存在返回false
        Integer result = baseMapper.deleteById(id);
        return null != result && result > 0;
    }*/

}
