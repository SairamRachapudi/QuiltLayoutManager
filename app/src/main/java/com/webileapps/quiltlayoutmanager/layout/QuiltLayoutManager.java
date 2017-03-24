package com.webileapps.quiltlayoutmanager.layout;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import com.webileapps.quiltlayoutmanager.util.CellView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Sairam Rachapudi on 22/3/17.
 */

public class QuiltLayoutManager extends RecyclerView.LayoutManager  {
    private SparseArray<View> viewCache = new SparseArray<>();
    private static QuiltLayoutManager.Span[] spans = new QuiltLayoutManager.Span[3];
    private static int layoutHeight, layoutWidth, cellSize;
    private static int DIRECTION_UP = 0, DIRECTION_DOWN = 1;
    private static int PREVIOUS_DIRECTION = DIRECTION_DOWN;
    private static LinkedHashMap<Integer,Bound> viewBoundsMap = new LinkedHashMap<>();
    private int mFirstVisiblePosition=-1;

    /*
    This method is used to reset the variables as we shouldn't store much previous state of RecyclerView
     */
    private void initializeValues() {
        layoutHeight = getHeight();
        layoutWidth = getWidth();
        cellSize = layoutWidth / 3;
        for (int i = 0; i < spans.length; i++) {
            spans[i] = new QuiltLayoutManager.Span();
            spans[i].totalHeight = layoutHeight + 2 * cellSize;
            spans[i].right = (i + 1) * cellSize;
            spans[i].left = i * cellSize;
        }
    }

    /*
    This is main method which calculates the Bounds of each view and store them in HashMap<AdapterPos,Bound>
     */
    private void calculateBound1sForViews(RecyclerView.Recycler recycler) {
        for(int i=0;i<getItemCount();i++){
            View view = recycler.getViewForPosition(i);
             Bound bound = allocateSpace(view);
            if(bound !=null){
                viewBoundsMap.put(getPosition(view),bound);
            }
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        initializeValues();
        detachAndScrapAttachedViews(recycler);
        calculateBound1sForViews(recycler);
        fillDown(recycler, state, 0);
    }

    /*
     This method is used to fill the views on screen when user has scrolled downwards
     */
    private void fillDown(RecyclerView.Recycler recycler, RecyclerView.State state, int delta) {
        viewCache.clear();
        int  anchorPos = mFirstVisiblePosition;
        boolean fillDown = true;
        int pos;
        int totalArea = getWidth() * getHeight(),calculatedArea = 0;

        if(anchorPos == -1){
            anchorPos = 0;
        }

        // Store Views in Cache
        for(int i=0;i<getChildCount();i++){
            viewCache.put(getPosition(getChildAt(i)),getChildAt(i));
        }

        // Detach and Scrap Previously attached Views
        for(int i=0;i<viewCache.size();i++){
            detachView(viewCache.valueAt(i));
        }

        pos = anchorPos;

        while(fillDown && pos < getItemCount()){
            View view = viewCache.get(pos);
            if(view == null){
                view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithMargins(view,view.getLayoutParams().width,view.getLayoutParams().height);
                Bound bound = viewBoundsMap.get(pos);
                layoutDecorated(view,bound.left,bound.top,bound.right,bound.bottom);
                calculatedArea += getDecoratedMeasuredHeight(view) * getDecoratedMeasuredWidth(view);
            }else{
                attachView(view);
                viewCache.remove(pos);
                if(getDecoratedTop(view)<0){
                    calculatedArea += getDecoratedBottom(view) * getDecoratedMeasuredWidth(view);
                }else {
                    calculatedArea += getDecoratedMeasuredHeight(view) * getDecoratedMeasuredWidth(view);
                }
            }
            pos++;
            fillDown = calculatedArea <= totalArea + 9* cellSize * cellSize;
        }

        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }
    }

    /*
     This method is used to fill the views on screen when user has scrolled upwards
     */
    private void fillUp(RecyclerView.Recycler recycler, RecyclerView.State state, int delta) {
        viewCache.clear();
        int  anchorPos = mFirstVisiblePosition;
        boolean fillUp = true;
        int pos;
        int totalArea = getWidth() * getHeight(),calculatedArea = 0;

        if(anchorPos == -1){
            anchorPos = getChildCount()-1;
        }

        // Store Views in Cache
        for(int i=0;i<getChildCount();i++){
            viewCache.put(getPosition(getChildAt(i)),getChildAt(i));
        }

        // Detach and Scrap Previously attached Views
        for(int i=0;i<viewCache.size();i++){
            detachView(viewCache.valueAt(i));
        }

        pos = anchorPos;

        while(fillUp && pos >=0){
            View view = viewCache.get(pos);
            if(view == null){
                view = recycler.getViewForPosition(pos);
                addView(view);
                measureChildWithMargins(view,view.getLayoutParams().width,view.getLayoutParams().height);
                Bound bound = viewBoundsMap.get(pos);
                layoutDecorated(view,bound.left,bound.top,bound.right,bound.bottom);
                calculatedArea += getDecoratedMeasuredHeight(view) * getDecoratedMeasuredWidth(view);
            }else{
                if(getDecoratedTop(view)< layoutHeight) {
                    attachView(view);
                    viewCache.remove(pos);
                    if (getDecoratedTop(view) < 0) {
                        calculatedArea += getDecoratedBottom(view) * getDecoratedMeasuredWidth(view);
                    } else {
                        calculatedArea += getDecoratedMeasuredHeight(view) * getDecoratedMeasuredWidth(view);
                    }
                }
            }
            pos--;
            fillUp = calculatedArea <= totalArea + 9 * (cellSize * cellSize);
        }
        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }
    }

    private Bound allocateSpace(View view) {
        switch (((CellView) view.getTag()).cellType) {
            case 1:
                return allotSpaceForViewType12(view);
            case 2:
                return allotSpaceForViewType12(view);
            case 3:
                return allotSpaceForViewType34(view);
            case 4:
                return allotSpaceForViewType34(view);
        }

        return null;
    }

    /*
     Allocate the space for Type1 and Type 2 views
     Type1 - (X,X) (width,height)
     Type2 - (2X,X)
     */
    private Bound allotSpaceForViewType12(View viewItem) {
        CellView view = (CellView) viewItem.getTag();
        Bound Bound1 = null;
        int height = view.height;
        int[] availableLoc = new int[3];

            for (int i = 0; i < spans.length; i++) {
                QuiltLayoutManager.Span span = spans[i];
                availableLoc[i] = span.getFirstAvailableLocation(height);
            }
        int small = -1;
        int smallIndex = -1;
        for (int i = 0; i < availableLoc.length; i++) {
            if (availableLoc[i] != -1) {
                if (small == -1) {
                    small = availableLoc[i];
                    smallIndex = i;
                    continue;
                }
                if (availableLoc[i] < small) {
                    small = availableLoc[i];
                    smallIndex = i;
                }
            }
        }
        if (small == -1) {
            return null;
        }
        QuiltLayoutManager.Span span = spans[smallIndex];
        Cell cell1 = span.getCellForView(view);
        if (span.hasSpace() && cell1 != null) {
            Bound1 = new Bound(span.getLeft(), cell1.i, span.getRight(), cell1.j);
            viewItem.setTag(view);
            span.addCellToSpan(cell1, view.height);
        }

        return Bound1;
    }

    /*
     Allocate the space for Type3 and Type 4 views
     Type3 - (X,2X) (width,height)
     Type4 - (2X,2X)
     */
    private Bound allotSpaceForViewType34(View viewItem) {
        CellView view = (CellView) viewItem.getTag();
        Bound Bound1 = null;
        int height = view.height;
        int[] availableLoc = new int[3];
        availableLoc[0] = -1;
        availableLoc[1] = -1;
        availableLoc[2] = -1;
            for (int i = 0; i < spans.length - 1; i++) {
                QuiltLayoutManager.Span span = spans[i], adjacentSpan = spans[i + 1];
                int val1 = span.getFirstAvailableLocation(height);
                int val2 = adjacentSpan.getFirstAvailableLocation(height);
                if (val1 != -1 && val2 != -1) {
                    if (val1 == val2) {
                        availableLoc[i] = val1;
                    }
                }
            }

            //Find the least height from array
            int small = -1;
            int smallIndex = -1;
            for (int i = 0; i < availableLoc.length; i++) {
                if (availableLoc[i] != -1) {
                    if (small == -1) {
                        small = availableLoc[i];
                        smallIndex = i;
                        continue;
                    }
                    if (availableLoc[i] < small) {
                        small = availableLoc[i];
                        smallIndex = i;
                    }
                }
            }
            if (small == -1) {
                for (int i = 0; i < spans.length - 1; i++) {
                    QuiltLayoutManager.Span span = spans[i];
                    QuiltLayoutManager.Span adjacentSpan = spans[i + 1];
                    Cell cell1 = span.getCellForView(view);
                    if (span.hasSpace() && cell1 != null) {
                        if (adjacentSpan.checkSpaceIfAvailable(cell1)) {
                            Bound1 = new Bound(span.getLeft(), cell1.i, adjacentSpan.getRight(), cell1.j);
                            span.addCellToSpan(cell1, view.height);
                            adjacentSpan.addCellToSpan(cell1, view.height);
                            break;
                        }
                    }
                }
                return Bound1;
            }
            int i = smallIndex;
            QuiltLayoutManager.Span span = spans[i];
            QuiltLayoutManager.Span adjacentSpan = spans[i + 1];
            Cell cell1 = span.getCellForView(view);
            if (span.hasSpace() && cell1 != null) {
                Cell cell2 = adjacentSpan.getCellForView(view);
                if (adjacentSpan.hasSpace() && cell2 != null) {
                    Bound1 = new Bound(span.getLeft(), small, adjacentSpan.getRight(), small + view.height);
                    viewItem.setTag(view);
                    span.addCellToSpan(cell1, view.height);
                    adjacentSpan.addCellToSpan(cell1, view.height);
                }
            }

        return Bound1;
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        return params;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int delta = scrollVerticallyInternal(dy, recycler, state);
        return delta;
    }

    private int scrollVerticallyInternal(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int childCount = getChildCount();
        int itemCount = getItemCount();
        if (childCount == 0) {
            return 0;
        }
        int direction = dy>0?DIRECTION_DOWN:DIRECTION_UP;

        if(isAllDisplayed(direction)){
            return 0;
        }

        int delta = 0;
        if (dy < 0) {
            int scrollState = getState(direction);
            View firstView = null;
            if(scrollState == 2){
                firstView = getChildAt(0);
            }else{
                firstView = getChildAt(getChildCount()-1);
            }
            int firstViewAdapterPos = getPosition(firstView);
            if (firstViewAdapterPos > 0) {
                delta = dy;
            } else {
                int viewTop = getDecoratedTop(firstView);
                delta = Math.max(viewTop, dy);
            }
            offsetChildrenVertical(-delta);
            setScrollDirection(DIRECTION_UP);
            computeTotalOffset(DIRECTION_UP,delta);
            fillUp(recycler,state,delta);
        } else if (dy > 0) {
            View lastView = getBottomView();
            int lastViewAdapterPos = getPosition(lastView);
            if (lastViewAdapterPos < itemCount - 1) {
                delta = dy;
            } else {
                int viewBottom = getDecoratedBottom(lastView);
                int parentBottom = getHeight();
                delta = Math.min(viewBottom - parentBottom, dy);
            }
            offsetChildrenVertical(-delta);
            setScrollDirection(DIRECTION_DOWN);
            computeTotalOffset(DIRECTION_DOWN,delta);
            fillDown(recycler, state,delta);
        }
        return delta;
    }

    /*
     Checking whether all Items got displayed in layout till last adapter position
     */
    private boolean isAllDisplayed(int direction) {
        int lastItem = getItemCount()-1;
        boolean allItemsShowed = false;
        List<View> overflowedViews = new ArrayList<>();
        if(direction == DIRECTION_DOWN) {
            for (int i = 0; i < getChildCount(); i++) {
                if (getPosition(getChildAt(i)) == lastItem) {
                    // we have showed all items and screen is in the edge check if any view in overflow
                    allItemsShowed = true;
                }
            }

            if (allItemsShowed) {
                for (int i = 0; i < getChildCount(); i++) {
                    if (getDecoratedBottom(getChildAt(i)) > layoutHeight) {
                        overflowedViews.add(getChildAt(i));
                    }
                }

                if (overflowedViews.size() == 0) {
                    return true;
                }

                /*
                   Not used this maxBottom for now
                 */
                int maxBottom = layoutHeight;
                for (View view : overflowedViews) {
                    if (getDecoratedBottom(view) > layoutHeight) {
                        maxBottom = getDecoratedBottom(view);
                    }
                }

            } else {
                return false;
            }
        }
        return false;
    }

    /*
     Get the bottom most view visible in screen
     */
    private View getBottomView() {
        int bottom = getHeight();
        View bottomView = null;
        for (int i = 0; i < getChildCount(); i++) {
            if (getDecoratedBottom(getChildAt(i)) >= bottom) {
                bottom = getDecoratedBottom(getChildAt(i));
                bottomView = getChildAt(i);
            }
        }
        if (bottomView == null) {
            bottomView = getChildAt(getChildCount() - 1);
        }
        return bottomView;
    }

    /*
     Setting the scroll direction
     Scroll Direction is of 2 types DIRECTION_UP and DIRECTION_DOWN
     */
    private void setScrollDirection(int direction){
        int state = getState(direction);
        PREVIOUS_DIRECTION = direction;
        computeFirstVisiblePosition(state);
    }

    private void computeTotalOffset(int direction, int offset) {
        if(direction == DIRECTION_DOWN){
            for(Bound bound: viewBoundsMap.values()){
                bound.top -= offset;
                bound.bottom -= offset;
            }
        }else{
            for(Bound bound: viewBoundsMap.values()){
                bound.top -= offset;
                bound.bottom -= offset;
            }
        }
    }

    /*
      Calculating the first visible adapter position for DIRECTION_DOWN scroll and
      last bottom view for DIRECTION_UP scroll
      */
    private void computeFirstVisiblePosition(int state) {
        switch (state){
            case 1:
                for(int i=0;i<getChildCount()-1;i++){
                    if(getDecoratedBottom(getChildAt(i))>=0){
                        mFirstVisiblePosition = getPosition(getChildAt(i));
                        return;
                    }
                }
                break;
            case 2:
                for(int i=getChildCount()-1;i>=0;i--){
                    if(getDecoratedTop(getChildAt(i))<= layoutHeight){
                        mFirstVisiblePosition = getPosition(getChildAt(i));
                        return;
                    }
                }
                break;
            case 3:
                for(int i=0;i<getChildCount()-1;i++){
                    if(getDecoratedTop(getChildAt(i))<= layoutHeight){
                        mFirstVisiblePosition = getPosition(getChildAt(i));
                        return;
                    }
                }
                break;
            case 4:
                for(int i=getChildCount()-1;i>=0;i--){
                    if(getDecoratedBottom(getChildAt(i))>=0){
                        mFirstVisiblePosition = getPosition(getChildAt(i));
                        return;
                    }
                }
                break;

        }
    }

    /*
        Get the current state of the  RecyclerView comaparing with the last scroll direction.
        These states are defined by me.
     */
    private int getState(int direction) {
        int state=1;
        if(PREVIOUS_DIRECTION == DIRECTION_DOWN && direction == DIRECTION_DOWN){
            state = 1;
        }else if(PREVIOUS_DIRECTION == DIRECTION_DOWN && direction == DIRECTION_UP){
            state = 2;
        }else if(PREVIOUS_DIRECTION == DIRECTION_UP && direction == DIRECTION_UP){
            state = 3;
        }else{
            state = 4;
        }
        return state;
    }

    /*
        Span is a 1/3rd part of device.
        Screen is divided into 3 vertical spans and views will be accomodated in these spans.
     */
    class Span {
        int heightFilled = 0;
        int totalHeight = 60;
        int left = 0, top, right, bottom = 0;
        List<Cell> cellsFilled = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }

        public int getTop() {
            return heightFilled;
        }

        public int getBottom() {
            return heightFilled;
        }

        public boolean hasSpace() {
            return true;
        }

        public int getFirstAvailableLocation(int height) {
            if (heightFilled == 0) {
                return 0;
            }

            if (cellsFilled.size() == 1) {
                if (hasSpace()) {
                    return heightFilled;
                }
            }
            Cell previousCell = new Cell(0, 0);
            for (Cell cell : cellsFilled) {
                if (cell.i - previousCell.j >= height) {
                    return previousCell.j;
                }
                previousCell = cell;
            }
            if (hasSpace()) {
                return heightFilled;
            }

            return -1;
        }

        public Cell getCellForView(CellView view) {
        int height = view.height;
            if (heightFilled == 0) {
                return new Cell(0, height);
            }
            if (cellsFilled.size() == 1) {
                if (hasSpace()) {
                    return new Cell(heightFilled, heightFilled + height);
                }
            }
            Cell previousCell = new Cell(0, 0);
            for (Cell cell : cellsFilled) {
                if (cell.i - previousCell.j >= height) {
                    return new Cell(previousCell.j, previousCell.j + height);
                }
                previousCell = cell;
            }
            if (hasSpace()) {
                return new Cell(heightFilled, heightFilled + height);
            }
            return null;
        }

        public boolean checkSpaceIfAvailable(Cell viewCell) {

            for (Cell cell : cellsFilled) {
                if (!(cell.j <= viewCell.i || cell.i >= viewCell.j)) {
                    if (cell.i == viewCell.i) {
                        return false;
                    }
                    if (cell.j == viewCell.j) {
                        return false;
                    }

                    if (cell.i < viewCell.i || viewCell.i < cell.j) {
                        return false;
                    }
                    if (cell.i < viewCell.j || viewCell.j < cell.j) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void addCellToSpan(Cell cell, int height) {
            addCellInPosition(cell);
            this.heightFilled += height;
        }

        public void addCellInPosition(Cell cell) {
            int pos = 0;

            if (cellsFilled.size() == 0) {
                cellsFilled.add(0, cell);
                return;
            }

            for (Cell view : cellsFilled) {
                if (!(cell.i >= view.j)) {
                    break;
                }
                pos = cellsFilled.indexOf(view);
            }
            cellsFilled.add(pos + 1, cell);

        }
    }
}


class Cell {
    int i;
    int j;

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
    }
}

class Bound {
    int left;
    int top;
    int right;
    int bottom;

    public Bound(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

}
