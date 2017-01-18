package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.STRING;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_PARENT_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLLAPSEALLALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPANDALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_TREEDIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandPartial;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedDiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.TreeDiffAggregation;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.DiffAggregationTreeItem;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class TreeDiffViewController extends ProfileDiffViewController<AggregationProfile>
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
    private TreeTableView<AggregatedDiffNode<String>> diffTree;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, String> methodColumn;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> baseSelfPct;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> newSelfPct;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> selfPctDiff;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> baseTotalPct;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> newTotalPct;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> totalPctDiff;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> baseSelfCnt;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> newSelfCnt;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> selfCntDiff;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> baseTotalCnt;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> newTotalCnt;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> totalCntDiff;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> baseParentCnt;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> newParentCnt;
    @FXML
    private TreeTableColumn<AggregatedDiffNode<String>, Number> parentCntDiff;

    private TreeDiffAggregation<String> diff;

    @Override
    @FXML
    protected void initialize()
    {
        diff = new TreeDiffAggregation<>();

        super.initialize(
            profileContext -> profileContext.profileProperty(),
            filterButton,
            quickFilterButton,
            quickFilterText);
    }

    @Override
    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        super.setProfileContexts(baseContext, newContext);

        initializeTable();
    }

    // Initialization Helper Methods

    private void initializeTable()
    {
        methodColumn.setCellValueFactory(
            data -> new ReadOnlyStringWrapper(
                data.getValue() == null ? null : data.getValue().getValue().getKey()));
        methodColumn.setCellFactory(col -> new MethodNameTreeTableCell<>(appCtx()));

        cfgPctCol(baseSelfPct, "baseSelfPct", baseCtx(), getText(COLUMN_SELF_PCT));
        cfgPctCol(newSelfPct, "newSelfPct", newCtx(), getText(COLUMN_SELF_PCT));
        cfgPctDiffCol(selfPctDiff, "selfPctDiff", getText(COLUMN_SELF_PCT_DIFF));

        cfgPctCol(baseTotalPct, "baseTotalPct", baseCtx(), getText(COLUMN_TOTAL_PCT));
        cfgPctCol(newTotalPct, "newTotalPct", newCtx(), getText(COLUMN_TOTAL_PCT));
        cfgPctDiffCol(totalPctDiff, "totalPctDiff", getText(COLUMN_TOTAL_PCT_DIFF));

        cfgCntCol(baseSelfCnt, "baseSelfCnt", baseCtx(), getText(COLUMN_SELF_CNT));
        cfgCntCol(newSelfCnt, "newSelfCnt", newCtx(), getText(COLUMN_SELF_CNT));
        cfgCntDiffCol(selfCntDiff, "selfCntDiff", getText(COLUMN_SELF_CNT_DIFF));

        cfgCntCol(baseTotalCnt, "baseTotalCnt", baseCtx(), getText(COLUMN_TOTAL_CNT));
        cfgCntCol(newTotalCnt, "newTotalCnt", newCtx(), getText(COLUMN_TOTAL_CNT));
        cfgCntDiffCol(totalCntDiff, "totalCntDiff", getText(COLUMN_TOTAL_CNT_DIFF));

        cfgCntCol(baseParentCnt, "baseRefCnt", baseCtx(), getText(COLUMN_PARENT_CNT));
        cfgCntCol(newParentCnt, "newRefCnt", newCtx(), getText(COLUMN_PARENT_CNT));
        cfgCntDiffCol(parentCntDiff, "refCntDiff", getText(COLUMN_PARENT_CNT));
    }

    private void updateDiff(AggregationProfile profile, boolean base)
    {
        // CopyAndFilterProfileTask task = new CopyAndFilterProfileTask(
        // profile,
        // getAdjustedProfileFilter());
        // task.setOnSucceeded(state ->
        // {
        // // No need to worry about concurrency here, since this (the code for
        // // onSucceeded()) will be executed on the FX thread. So even though
        // // in the diff 2 tasks might execute concurrently during refresh(),
        // // the resulting update calls in this if-statement won't execute
        // // concurrently.
        // if (base)
        // {
        // diff.updateForBase(task.getValue());
        // diffTree.setRoot(new DiffTreeItem(diff));
        // expandPartial(diffTree.getRoot(), 2);
        // }
        // else
        // {
        // diff.updateForNew(task.getValue());
        // diffTree.setRoot(new DiffTreeItem(diff));
        // expandPartial(diffTree.getRoot(), 2);
        // }
        // });
        // appCtx().execute(task);

        if (profile != null)
        {
            if (base)
            {
                diff.addBase(profile.getTreeAggregation());
            }
            else
            {
                diff.addNew(profile.getTreeAggregation());
            }
            diffTree.setRoot(new DiffAggregationTreeItem(diff));
            expandPartial(diffTree.getRoot(), 2);
        }
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
        info(diffTree, INFO_TABLE_TREEDIFF);
    }

    @Override
    protected void initializeHandlers()
    {
        expandAllButton.setOnAction(event -> expandFully(diffTree.getRoot()));
        collapseAllButton.setOnAction(
            event -> diffTree.getRoot().getChildren().stream().forEach(TreeUtil::collapseFully));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        diff = new TreeDiffAggregation<>();
        updateDiff(getBaseTarget(), true);
        updateDiff(getNewTarget(), false);
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING };
    }
}
