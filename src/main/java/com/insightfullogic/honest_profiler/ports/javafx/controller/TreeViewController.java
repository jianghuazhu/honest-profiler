/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.STRING;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.THREAD_SAMPLE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.TIME_SHARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_PARENT_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLLAPSEALLALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPANDALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedNode;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.TreeViewCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.AggregationTreeItem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class TreeViewController extends ProfileViewController<AggregationProfile>
{
    @FXML
    private Button filterButton;
    @FXML
    private Button expandAllButton;
    @FXML
    private Button collapseAllButton;
    @FXML
    private TextField quickFilterText;
    @FXML
    private Button quickFilterButton;

    @FXML
    private TreeTableView<AggregatedNode<String>> treeView;
    @FXML
    private TreeTableColumn<AggregatedNode<String>, String> methodColumn;
    @FXML
    private TreeTableColumn<AggregatedNode<String>, AggregatedNode<String>> percentColumn;
    @FXML
    private TreeTableColumn<AggregatedNode<String>, Number> totalPct;
    @FXML
    private TreeTableColumn<AggregatedNode<String>, Number> selfPct;
    @FXML
    private TreeTableColumn<AggregatedNode<String>, Number> totalCnt;
    @FXML
    private TreeTableColumn<AggregatedNode<String>, Number> selfCnt;
    @FXML
    private TreeTableColumn<AggregatedNode<String>, Number> parentCnt;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(
            profileContext -> profileContext.profileProperty(),
            filterButton,
            quickFilterButton,
            quickFilterText);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        initializeTable();
    }

    // Initialization Helper Methods

    private void initializeTable()
    {
        methodColumn.setCellFactory(column -> new MethodNameTreeTableCell<>(appCtx()));
        methodColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("key"));

        percentColumn.setCellFactory(param -> new TreeViewCell());

        cfgPctCol(totalPct, "totalCntPct", prfCtx(), COLUMN_TOTAL_PCT);
        cfgPctCol(selfPct, "selfCntPct", prfCtx(), COLUMN_SELF_PCT);
        cfgCntCol(totalCnt, "totalCnt", prfCtx(), COLUMN_TOTAL_CNT);
        cfgCntCol(selfCnt, "selfCnt", prfCtx(), COLUMN_SELF_CNT);
        cfgCntCol(parentCnt, "parentCount", prfCtx(), COLUMN_PARENT_CNT);
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(expandAllButton, INFO_BUTTON_EXPANDALL);
        info(collapseAllButton, INFO_BUTTON_COLLAPSEALLALL);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(treeView, INFO_TABLE_TREE);
    }

    @Override
    protected void initializeHandlers()
    {
        expandAllButton.setOnAction(event -> expandFully(treeView.getRoot()));

        collapseAllButton.setOnAction(
            event -> treeView.getRoot().getChildren().stream().forEach(TreeUtil::collapseFully));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        // CopyAndFilterProfileTask task = new CopyAndFilterProfileTask(
        // getTarget(),
        // getAdjustedProfileFilter());
        // task.setOnSucceeded(state ->
        // rootNode.update(task.getValue().getTrees()));
        // appCtx().execute(task);

        treeView.setRoot(new AggregationTreeItem(getTarget().getTreeAggregation()));
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING, THREAD_SAMPLE, TIME_SHARE };
    }
}
