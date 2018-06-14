package com.wangjikai;

import com.wangjikai.po.NovelContent;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author 22717.
 * @date 2018/4/2.
 * Description:
 */
public interface NovelContentMongoRepository extends MongoRepository<NovelContent,Long> {
}
