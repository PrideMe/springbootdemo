package com.wangjikai;

import com.wangjikai.po.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 22717.
 * @date 2018/4/11.
 * Description:
 */
@Repository
public interface NovelRepository extends JpaRepository<Novel,Integer> {
    /**
     * 根据书名或作者模糊查询小说
     * @param name 小说名
     * @param author 作者名
     * @return 查询到的小说列表
     */
    List<Novel> findNovelsByNameContainingOrAuthorContaining(String name, String author);
}
