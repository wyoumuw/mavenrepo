package com.youmu.win.m2repo.aop;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.WeakHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.youmu.common.Loggable;
import com.youmu.win.m2repo.ProcessService;
import com.youmu.win.m2repo.model.IndexPageModel;
import com.youmu.win.m2repo.model.VersionItemModel;
import com.youmu.win.m2repo.utils.JSONUtils;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/10
 */
public class ProcessServiceAdvice implements ProcessService, Loggable {

    public static final Charset fileCharset = Charset.forName("UTF-8");

    public static final String SEPARATOR = "Youmu-Youmu";
    public static final String DESC_FILE_NAME = "describe";

    public static final String TEMP_DIR = "E:\\m2rep";

    private TypeReference<List<VersionItemModel>> versionItemModelTypeReference = new TypeReference<List<VersionItemModel>>() {
    };

    WeakHashMap<String, List<VersionItemModel>> versionMap = new WeakHashMap<>();
    ProcessService processService;

    public ProcessServiceAdvice(ProcessService processService) {
        if (null == processService) {
            throw new NullPointerException("processService can not be null!!");
        }
        this.processService = processService;
    }

    @Override
    public IndexPageModel getIndex(String query, int page) {
        return processService.getIndex(query, page);
    }

    @Override
    public List<VersionItemModel> getVersion(String subUrl) {
        getLog().info("getVersion from cache");
        List<VersionItemModel> list = versionMap.get(subUrl);
        if (null != list) {
            return list;
        }
        list = readFromFile(subUrl);
        if (null == list) {
            list = processService.getVersion(subUrl);
            try {
                saveToFile(list, subUrl);
            } catch (IOException e) {
                getLog().error("", e);
            }
        }
        versionMap.put(subUrl, list);
        return list;
    }

    @Override
    public VersionItemModel fillDependency(VersionItemModel versionItemModel) {
        getLog().info("fillDependency from cache");

        List<VersionItemModel> list = versionMap.get(versionItemModel.getSubUrl().substring(0,
                versionItemModel.getSubUrl().lastIndexOf("/")));
        VersionItemModel cacheModel = null;
        if (null != list) {
            for (VersionItemModel itemModel : list) {
                if (StringUtils.equals(versionItemModel.getName(), itemModel.getName())) {
                    cacheModel = itemModel;
                    if (StringUtils.isNotBlank(itemModel.getDependency())) {
                        versionItemModel.setDependency(itemModel.getDependency());
                        return versionItemModel;
                    } else {
                        break;
                    }
                }
            }
        }
        VersionItemModel model;
        try {
            model = readDependencyFromFile(versionItemModel);
        } catch (IOException e) {
            getLog().error("", e);
            return null;
        }
        if (null == model) {
            model = processService.fillDependency(versionItemModel);
            try {
                saveDependencyToFile(model);
            } catch (IOException e) {
                getLog().error("", e);
            }
        }
        if (null != cacheModel) {
            cacheModel.setDependency(model.getDependency());
        }
        return model;
    }

    private void saveToFile(List<VersionItemModel> models, String subUrl) throws IOException {
        if (CollectionUtils.isEmpty(models)) {
            return;
        }
        String dirPath = TEMP_DIR + subUrl;
        File dir = new File(dirPath);
        dir.mkdirs();
        File describeFile = new File(dirPath + File.separator + DESC_FILE_NAME);
        if (describeFile.exists()) {
            describeFile.delete();
        }
        describeFile.createNewFile();
        FileUtils.write(describeFile, JSONUtils.serialize(models), fileCharset);
    }

    private List<VersionItemModel> readFromFile(String subUrl) {
        String dirPath = TEMP_DIR + subUrl;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return null;
        }
        File descFile = new File(dirPath + File.separator + DESC_FILE_NAME);
        if (!descFile.exists()) {
            return null;
        }
        List<VersionItemModel> list = null;
        try {
            String desc = FileUtils.readFileToString(descFile, fileCharset);
            list = JSONUtils.deserialize(desc, versionItemModelTypeReference);
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    private void saveDependencyToFile(VersionItemModel versionItemModel) throws IOException {
        if (StringUtils.isBlank(versionItemModel.getDependency())) {
            return;
        }
        File dependencyFile = new File(TEMP_DIR + versionItemModel.getSubUrl());
        File dir = dependencyFile.getParentFile();
        dir.mkdirs();
        FileUtils.write(dependencyFile, versionItemModel.getDependency(), fileCharset);
    }

    private VersionItemModel readDependencyFromFile(VersionItemModel versionItemModel)
            throws IOException {
        if (null == versionItemModel) {
            return null;
        }
        File dependencyFile = new File(TEMP_DIR + versionItemModel.getSubUrl());
        if (!dependencyFile.exists()) {
            return null;
        }
        String dependency = FileUtils.readFileToString(dependencyFile, fileCharset);
        if (StringUtils.isBlank(dependency)) {
            return null;
        }
        versionItemModel.setDependency(dependency);
        return versionItemModel;

    }
}
