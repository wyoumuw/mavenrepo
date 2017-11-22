package com.youmu.win.m2repo;

import java.util.List;

import com.youmu.win.m2repo.model.IndexPageModel;
import com.youmu.win.m2repo.model.VersionItemModel;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/10
 */
public interface ProcessService {
    public IndexPageModel getIndex(String query, int page);

    public List<VersionItemModel> getVersion(String subUrl);

    public VersionItemModel fillDependency(VersionItemModel versionItemModel);
}
