package com.youmu.win.m2repo.model;

import java.util.List;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class IndexPageModel {

    public static final int DEFAULT_PAGE_SIZE = 10;
    private List<IndexItemModel> list;
    private int pages;
    private int total;

    public IndexPageModel() {
    }

    public IndexPageModel(List<IndexItemModel> list, int total) {
        this.list = list;
        this.pages = ((total - 1) / DEFAULT_PAGE_SIZE) + 1;
        this.total = total;
    }

	public static void main(String[] args) {
		System.out.println(((11 - 1) / DEFAULT_PAGE_SIZE) + 1);
		System.out.println(((10 - 1) / DEFAULT_PAGE_SIZE) + 1);
		System.out.println(((21 - 1) / DEFAULT_PAGE_SIZE) + 1);
	}

    public List<IndexItemModel> getList() {
        return list;
    }

    public void setList(List<IndexItemModel> list) {
        this.list = list;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
