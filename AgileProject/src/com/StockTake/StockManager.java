package com.StockTake;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StockManager extends Application {
	// private Finance stockObj;
	private HashMap<Finance, Float> portfolio = new HashMap<Finance, Float>();
	private HashMap<String, String> stockNamesLong = new HashMap<String, String>();
	List<String> stockNames = new ArrayList<String>();
	FeedParser newParse = new FeedParser();

	public float RUN_CONST = 1.2f;
	public float ROCKET_CONST = 1.2f;
	public float PLUMMET_CONST = 0.1f;
	
	public float getRunValue()
	{
		return RUN_CONST;
	}
	
	public float getRocketValue()
	{
		return ROCKET_CONST;
	}
	public float getPlummetValue()
	{
		return PLUMMET_CONST;
	}
	
	public void setRunValue(float run)
	{
		RUN_CONST = run;
	}
	
	public void setRocketValue(float rocket)
	{
		ROCKET_CONST = rocket;
	}

	public void setPlummetValue(float plummet)
	{
		PLUMMET_CONST = plummet;
	}

	private String myState;

	public String getState() {
		return myState;
	}

	public void setState(String s) {
		myState = s;
	}

	public void clearPortfolio() {
		portfolio.clear();
		stockNames.clear();
		stockNamesLong.clear();
	}

	public Finance createFinanceObject(String stockCode) throws IOException {

		Finance newStock = new Finance();

		newParse.getFeed(newStock, stockCode);
		newStock.calcRun(RUN_CONST);
		newStock.calcRocketPlummet(ROCKET_CONST, PLUMMET_CONST);

		return newStock;

	}

	public boolean addPortfolioEntry(String stockCode, String stockNameLong, int numberOfShares) throws IOException {

		float shareQuantity = (float) numberOfShares;
		Finance stockObj = createFinanceObject(stockCode);
		if (portfolio.containsKey(stockObj)) {
			return false;
		}
		stockObj.setName(stockNameLong);
		portfolio.put(stockObj, shareQuantity);
		stockNames.add(stockObj.getName());
		stockNamesLong.put(stockObj.getName(), stockNameLong);
		return true;
	}

	public float getPortfolioTotal() {
		float value = 0;
		if (portfolio.isEmpty()) {
			return 0;
		}
		for (Finance stockObj : portfolio.keySet()) {
			value += stockObj.getLast() * portfolio.get(stockObj);
		}
		return value;
	}

	@SuppressWarnings("rawtypes")
	public void summaryTable(Activity contextActivity) {

		TableLayout table = (TableLayout) contextActivity.findViewById(R.id.tableLayout1); // Find TableLayout defined in xml

		table.setStretchAllColumns(true);
		table.setShrinkAllColumns(true);

		// Stock Loop
		int stockCount = portfolio.size();
		int stockCounter = 0;

		TableRow[] rowStock = new TableRow[stockCount];
		TextView[] stockName = new TextView[stockCount];
		TextView[] stockShares = new TextView[stockCount];
		TextView[] stockValue = new TextView[stockCount];
		TextView[] stockTotal = new TextView[stockCount];

		TableRow rowTotal = new TableRow(contextActivity);
		TextView portfolioTotal = new TextView(contextActivity);

		NavigableMap sortedMap = sortPortfolioByValue();
		// Now sort...
		Iterator<?> it = sortedMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String currStockName = pairs.getValue().toString();

			Finance stockObj = null;
			for (Finance thisObj : portfolio.keySet()) {
				if (thisObj.getName().equals(currStockName)) {
					stockObj = thisObj;
					break; 
				}
			}

			rowStock[stockCounter] = new TableRow(contextActivity);
			stockName[stockCounter] = new TextView(contextActivity);
			stockShares[stockCounter] = new TextView(contextActivity);
			stockValue[stockCounter] = new TextView(contextActivity);
			stockTotal[stockCounter] = new TextView(contextActivity);

			float thisStockValue = stockObj.getLast();

			// half up rounding mode - so reduces errors to +/- £1
			BigDecimal stockValueRounded = new BigDecimal(
					Double.toString(thisStockValue));
			stockValueRounded = stockValueRounded.setScale(0,
					BigDecimal.ROUND_UP);
			float subTotal = (portfolio.get(stockObj) * thisStockValue);

			// String longName =
			// stockNamesLong.get(stockObj.getName().toString());
			String longName = currStockName;

			stockName[stockCounter].setText(longName);
			stockName[stockCounter]
					.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			// stockName[stockCounter].setTextColor(Color.rgb(58, 128, 255));
			stockName[stockCounter].setTextSize(24f);
			stockName[stockCounter].setHeight(130);
			stockName[stockCounter].setWidth(500);
			stockName[stockCounter].setGravity(Gravity.LEFT
					| Gravity.CENTER_VERTICAL);


			stockTotal[stockCounter].setText("£  "
					+ String.format("%,3.0f", subTotal));
			stockTotal[stockCounter].setGravity(Gravity.RIGHT
					| Gravity.CENTER_VERTICAL);
			stockTotal[stockCounter].setTextSize(24f);
			stockTotal[stockCounter].setSingleLine(true);

			rowStock[stockCounter].addView(stockName[stockCounter]);
			// rowStock[stockCounter].addView(stockShares[stockCounter]);
			// rowStock[stockCounter].addView(stockValue[stockCounter]);
			rowStock[stockCounter].addView(stockTotal[stockCounter]);

			table.addView(rowStock[stockCounter]);

			stockCounter++;

		}

		String totalVal = "Total Value:     £  "
				+ String.format("%,.0f", getPortfolioTotal());
		portfolioTotal.setText(totalVal);
		portfolioTotal.setTextSize(28f);
		portfolioTotal.setHeight(100);
		portfolioTotal.setTextColor(Color.rgb(168, 232, 37));
		portfolioTotal.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);

		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 4;

		rowTotal.addView(portfolioTotal, params);
		table.addView(rowTotal);

	}

	@SuppressWarnings("rawtypes")
	public NavigableMap sortPortfolioByValue() {
		TreeMap<Float, String> portfolioValue = new TreeMap<Float, String>();
		Finance currentStock = new Finance();
		float stocksHeld;
		float total;

		Iterator it = portfolio.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			currentStock = (Finance) pairs.getKey();
			stocksHeld = (Float) pairs.getValue();
			total = stocksHeld * currentStock.getLast();

			portfolioValue.put(total, currentStock.getName());
		}
		NavigableMap mapMap = portfolioValue.descendingMap();

		return mapMap;
	}

	public int volumeTable(Activity contextActivity) {
		
		updateRuns();

		TableLayout table = (TableLayout) contextActivity
				.findViewById(R.id.tableLayout2); // Find
													// TableLayout
													// defined
													// in
													// main.xml

		table.setStretchAllColumns(true);
		table.setShrinkAllColumns(true);

		int stockCount = 6;
		int stockCounter = 0;
		int runs = 0;

		TableRow[] rowRun = new TableRow[stockCount];
		TextView[] runStock = new TextView[stockCount];
		TextView[] runLabel = new TextView[stockCount];

		// Now sort...
		Collections.sort(stockNames);
		for (String currStockName : stockNames) // Sorted list of names
		{

			Finance stockObj = null;
			for (Finance thisObj : portfolio.keySet()) {
				if (thisObj.getName().equals(currStockName)) {
					stockObj = thisObj;
					break; // fail fast
				}
			}

			rowRun[stockCounter] = new TableRow(contextActivity);
			runStock[stockCounter] = new TextView(contextActivity);
			runLabel[stockCounter] = new TextView(contextActivity);
			runStock[stockCounter].setText(stockNamesLong.get(stockObj
					.getName().toString()));
			runStock[stockCounter].setTextSize(26f);
			runStock[stockCounter].setHeight(130);
			runStock[stockCounter].setGravity(Gravity.CENTER_VERTICAL);
			runLabel[stockCounter].setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			runLabel[stockCounter].setTextSize(26f);
			runLabel[stockCounter].setHeight(130);
			runLabel[stockCounter].setGravity(Gravity.CENTER_VERTICAL
					| Gravity.RIGHT);

			if (stockObj.isRun() && !stockObj.isPlummet()
					&& !stockObj.isRocket()) {

				runLabel[stockCounter].setText(Html
						.fromHtml("<font color=\"#CCCC33\">Run</font>"));
				rowRun[stockCounter].addView(runStock[stockCounter]);
				rowRun[stockCounter].addView(runLabel[stockCounter]);
				runs++;
			} else if (stockObj.isRun() && stockObj.isPlummet()) {
				runLabel[stockCounter]
						.setText(Html
								.fromHtml("<font color=\"#CCCC33\">Run</font> & <font color='red'>Plummet</font>"));
				rowRun[stockCounter].addView(runStock[stockCounter]);
				rowRun[stockCounter].addView(runLabel[stockCounter]);
				runs++;
			} else if (stockObj.isRun() && stockObj.isRocket()) {
				runLabel[stockCounter]
						.setText(Html
								.fromHtml("<font color=\"#CCCC33\">Run</font> & <font color='green'>Rocket</font>"));
				rowRun[stockCounter].addView(runStock[stockCounter]);
				rowRun[stockCounter].addView(runLabel[stockCounter]);
				runs++;
			} else if (!stockObj.isRun() && stockObj.isPlummet()) {
				runLabel[stockCounter].setText(Html
						.fromHtml("<font color='red'>Plummet</font>"));
				rowRun[stockCounter].addView(runStock[stockCounter]);
				rowRun[stockCounter].addView(runLabel[stockCounter]);
				runs++;
			} else if (!stockObj.isRun() && stockObj.isRocket()) {
				runLabel[stockCounter].setText(Html
						.fromHtml("<font color='green'>Rocket</font>"));
				rowRun[stockCounter].addView(runStock[stockCounter]);
				rowRun[stockCounter].addView(runLabel[stockCounter]);
				runs++;
			}

			table.addView(rowRun[stockCounter]);

			stockCounter++;

		}

		return runs;

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateRuns()
	{
		Iterator iterate = portfolio.entrySet().iterator();
		while(iterate.hasNext())
		{
			Map.Entry<Finance, Float> entry = (Map.Entry<Finance, Float>)iterate.next();
			Finance stock = entry.getKey();
			
			stock.calcRun(RUN_CONST);
			stock.calcRocketPlummet(ROCKET_CONST, PLUMMET_CONST);
		}
	}

	public int rocketTable(Activity contextActivity) {

		TableLayout table = (TableLayout) contextActivity
				.findViewById(R.id.tableLayout3); // Find
													// TableLayout
													// defined
													// in
													// main.xml

		table.setStretchAllColumns(true);
		table.setShrinkAllColumns(true);

		int stockCount = 6;
		int stockCounter = 0;
		int rocketplummet = 0;

		TableRow[] rowRocket = new TableRow[stockCount];
		TextView[] rocketStock = new TextView[stockCount];
		TextView[] rocketState = new TextView[stockCount];

		// Now sort...
		Collections.sort(stockNames);
		for (String currStockName : stockNames) // Sorted list of names
		{

			Finance stockObj = null;
			for (Finance thisObj : portfolio.keySet()) {
				if (thisObj.getName().equals(currStockName)) {
					stockObj = thisObj;
					break; // fail fast
				}
			}

			rowRocket[stockCounter] = new TableRow(contextActivity);
			rocketStock[stockCounter] = new TextView(contextActivity);
			rocketState[stockCounter] = new TextView(contextActivity);
			rocketState[stockCounter].setGravity(Gravity.RIGHT);
			rocketState[stockCounter].setTextSize(20f);

			rocketStock[stockCounter].setTextSize(20f);
			rocketStock[stockCounter].setHeight(100);
			rocketStock[stockCounter].setGravity(Gravity.CENTER_VERTICAL);
			rocketStock[stockCounter].setText(stockNamesLong.get(stockObj
					.getName().toString()));

			if (stockObj.isRocket()) {
				rocketState[stockCounter].setText(Html
						.fromHtml("<font color='green'>Rocket</font>"));
				rowRocket[stockCounter].addView(rocketStock[stockCounter]);
				rowRocket[stockCounter].addView(rocketState[stockCounter]);
				rocketplummet++;
			} else if (stockObj.isPlummet()) {
				rocketState[stockCounter].setText(Html
						.fromHtml("<font color='red'>Plummet</font>"));
				rowRocket[stockCounter].addView(rocketStock[stockCounter]);
				rowRocket[stockCounter].addView(rocketState[stockCounter]);
				rocketplummet++;
			}

			table.addView(rowRocket[stockCounter]);

			stockCounter++;

		}
		return rocketplummet;
	}
}
