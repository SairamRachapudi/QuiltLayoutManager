package com.webileapps.quiltlayoutmanager.util;

/**
 * Created by sairam on 14/3/17.
 */

public class CellView {
    public int width;
    public int height;
    public int cellType;

    public CellView(int width, int height, int part){
        this.width = width;
        this.height = height;
        this.cellType = calculateCellType(width,height,part);
    }

    private int calculateCellType(int width,int height,int part){
        if(width/part>1){
            if(height/part>1){
                return  4;
            }
            return 3;
        }

        if(height/part>1){
            return 2;
        }
        return 1;
    }
}


