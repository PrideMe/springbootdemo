package com.wangjikai;

import com.wangjikai.po.NovelUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 22717.
 * @date 2018/4/2.
 * Description:
 */
@Repository
public interface NovelUrlRepository extends JpaRepository<NovelUrl,Integer> {
}
