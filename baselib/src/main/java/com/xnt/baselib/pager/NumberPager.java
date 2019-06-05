package com.xnt.baselib.pager;

/**
 * Created by yuhengye g10475 on 2018/6/14.
 **/
public class NumberPager extends Pager {

    public NumberPager(){
        this(1);
    }

    public NumberPager(int firstNumber){
        this(firstNumber, 20);
    }

    public NumberPager(int firstNumber, int pageSize){
        this.firstNumber = firstNumber;
        this.nextPageNumber = firstNumber;
        this.pageSize = pageSize;
    }

    private int firstNumber, nextPageNumber, pageSize;

    @Override
    public Integer getFirstPage() {
        return firstNumber;
    }

    @Override
    public Integer getNextPage() {
        return nextPageNumber;
    }

    @Override
    public Integer getPage(boolean isRefresh) {
        return (Integer)super.getPage(isRefresh);
    }

    @Override
    public void handlePage(boolean increase) {
        if(increase){
            nextPageNumber = firstNumber;
        }else{
            nextPageNumber++;
        }
    }

    public void setNextPageNumber(int nextPageNumber){
        this.nextPageNumber = nextPageNumber;
    }

    public Integer getNextPageNumber(){
        return this.nextPageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
