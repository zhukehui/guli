package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.common.base.util.ExcelImportUtil;
import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.vo.SubjectVo;
import com.atguigu.guli.service.edu.mapper.SubjectMapper;
import com.atguigu.guli.service.edu.service.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author eternity
 * @since 2019-11-20
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchImport(InputStream inputStream) throws Exception {

        //创建工具类对象
        ExcelImportUtil excelHSSFUtil = new ExcelImportUtil(inputStream);
        //获取工作表
        Sheet sheet = excelHSSFUtil.getSheet();
        //遍历数据行
        for (Row rowData : sheet) {

            //忽略标题行
            if (rowData.getRowNum() == 0) {
                continue;
            }

            //获取一级分类
            Cell levelOneCell = rowData.getCell(0);
            String levelOneValue = excelHSSFUtil.getCellValue(levelOneCell).trim();//trim()去空格
            if (levelOneCell == null || StringUtils.isEmpty(levelOneValue)) {
                continue;
            }

            //获取二级分类
            Cell levelTwoCell = rowData.getCell(1);
            String levelTwoValue = excelHSSFUtil.getCellValue(levelTwoCell).trim();//trim()去空格
            if (levelTwoCell == null || StringUtils.isEmpty(levelTwoValue)) {
                continue;
            }

            //判断一级分类是否重复
            Subject subject = this.getByTitle(levelOneValue);
            String parentId = null;
            if (subject == null) {
                //将一级分类存入数据库
                Subject subjectLevelOne = new Subject();
                subjectLevelOne.setTitle(levelOneValue);
                baseMapper.insert(subjectLevelOne);//添加
                parentId = subjectLevelOne.getId();
            } else {
                parentId = subject.getId();
            }

            //判断二级分类是否重复
            Subject subjectSub = this.getSubByTitle(levelTwoValue, parentId);
            Subject subjectLevelTwo = null;
            if (subjectSub == null) {
                //将二级分类存入数据库
                subjectLevelTwo = new Subject();
                subjectLevelTwo.setTitle(levelTwoValue);
                subjectLevelTwo.setParentId(parentId);
                baseMapper.insert(subjectLevelTwo);//添加
            }
        }
    }

    @Override
    public List<SubjectVo> nestedList() {
        //最终要的到的数据列表
        List<SubjectVo> subjectVoList = new ArrayList<>();

        //获取分类数据记录
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort", "id");
        List<Subject> subjectList = baseMapper.selectList(queryWrapper);

        //分别获取一级和二级分类数据记录
        List<Subject> subjectLevelOneList = new ArrayList<>();
        List<Subject> subjectLevelTwoList = new ArrayList<>();
        for (Subject subject : subjectList) {
            if (subject.getParentId().equals("0")) {
                subjectLevelOneList.add(subject);
            }else {
                subjectLevelTwoList.add(subject);
            }
        }

        //填充一级分类vo数据
        for (Subject subjectLevelOne : subjectLevelOneList) {

            //创建一级类别vo对象
            SubjectVo subjectVoLevelOne = new SubjectVo();
            BeanUtils.copyProperties(subjectLevelOne, subjectVoLevelOne);
            subjectVoList.add(subjectVoLevelOne);

            List<SubjectVo> subjectVoLevelTwoList = new ArrayList<>();
            for (Subject subjectLevelTwo : subjectLevelTwoList) {
                if(subjectLevelOne.getId().equals(subjectLevelTwo.getParentId())){

                    //创建二级类别vo对象
                    SubjectVo subjectVoLevelTwo = new SubjectVo();
                    BeanUtils.copyProperties(subjectLevelTwo, subjectVoLevelTwo);
                    subjectVoLevelTwoList.add(subjectVoLevelTwo);
                }
            }
            subjectVoLevelOne.setChildren(subjectVoLevelTwoList);

        }

        return subjectVoList;
    }

    /**
     * 根据分类名称查询这个一级分类中否存在
     *
     * @param title
     * @return
     */
    private Subject getByTitle(String title) {

        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", "0");//一级分类
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据分类名称和父id查询这个二级分类中否存在
     *
     * @param title
     * @return
     */
    private Subject getSubByTitle(String title, String parentId) {

        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", parentId);
        return baseMapper.selectOne(queryWrapper);
    }
}
