/*
 * This file is part of Grocy Android.
 *
 * Grocy Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grocy Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grocy Android. If not, see http://www.gnu.org/licenses/.
 *
 * Copyright (c) 2020-2024 by Patrick Zedler and Dominic Zedler
 * Copyright (c) 2024-2026 by Patrick Zedler
 */

package xyz.zedler.patrick.grocy.helper;

import android.app.Application;
import android.util.Log;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.json.JSONObject;
import xyz.zedler.patrick.grocy.database.AppDatabase;
import xyz.zedler.patrick.grocy.model.Product;
import xyz.zedler.patrick.grocy.model.ShoppingListItem;
import xyz.zedler.patrick.grocy.model.StockEntry;
import xyz.zedler.patrick.grocy.model.StockItem;

/**
 * LocalDataHelper handles all local database operations without any network calls.
 * This replaces DownloadHelper for local-only mode.
 */
public class LocalDataHelper {

  private static final String TAG = "LocalDataHelper";
  private final AppDatabase appDatabase;
  private final Application application;

  public interface OnSuccessListener {
    void onSuccess();
  }

  public interface OnErrorListener {
    void onError(Exception error);
  }

  public LocalDataHelper(Application application) {
    this.application = application;
    this.appDatabase = AppDatabase.getAppDatabase(application);
  }

  // PRODUCT OPERATIONS

  public void addProduct(Product product, OnSuccessListener onSuccess, OnErrorListener onError) {
    appDatabase.productDao().insertProduct(product)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Log.d(TAG, "Product added: " + product.getName());
              onSuccess.onSuccess();
            },
            error -> {
              Log.e(TAG, "Error adding product: ", error);
              onError.onError(new Exception(error));
            }
        );
  }

  public void updateProduct(Product product, OnSuccessListener onSuccess, OnErrorListener onError) {
    appDatabase.productDao().updateProduct(product)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Log.d(TAG, "Product updated: " + product.getName());
              onSuccess.onSuccess();
            },
            error -> {
              Log.e(TAG, "Error updating product: ", error);
              onError.onError(new Exception(error));
            }
        );
  }

  public void deleteProduct(int productId, OnSuccessListener onSuccess, OnErrorListener onError) {
    appDatabase.productDao().deleteById(productId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Log.d(TAG, "Product deleted: " + productId);
              onSuccess.onSuccess();
            },
            error -> {
              Log.e(TAG, "Error deleting product: ", error);
              onError.onError(new Exception(error));
            }
        );
  }

  // STOCK OPERATIONS

  public void purchaseProduct(int productId, double amount, String bestBeforeDate,
      OnSuccessListener onSuccess, OnErrorListener onError) {
    StockEntry entry = new StockEntry();
    entry.setProductId(productId);
    entry.setAmount(String.valueOf(amount));
    entry.setBestBeforeDate(bestBeforeDate);
    entry.setRowCreatedTimestamp(System.currentTimeMillis() + "");

    appDatabase.stockEntryDao().insertStockEntry(entry)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Log.d(TAG, "Stock entry added for product: " + productId);
              onSuccess.onSuccess();
            },
            error -> {
              Log.e(TAG, "Error adding stock entry: ", error);
              onError.onError(new Exception(error));
            }
        );
  }

  public void consumeProduct(int productId, double amount, OnSuccessListener onSuccess,
      OnErrorListener onError) {
    // Get the stock item for this product and reduce amount
    appDatabase.stockItemDao().getStockItemsByProductId(productId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            stockItems -> {
              if (stockItems != null && !stockItems.isEmpty()) {
                StockItem item = stockItems.get(0);
                double newAmount = Double.parseDouble(item.getAmount() != null ? item.getAmount() : "0") - amount;
                if (newAmount < 0) newAmount = 0;
                item.setAmount(String.valueOf(newAmount));
                appDatabase.stockItemDao().updateStockItem(item)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        () -> {
                          Log.d(TAG, "Stock consumed for product: " + productId);
                          onSuccess.onSuccess();
                        },
                        error -> {
                          Log.e(TAG, "Error consuming stock: ", error);
                          onError.onError(new Exception(error));
                        }
                    );
              } else {
                onError.onError(new Exception("Product not found in stock"));
              }
            },
            error -> {
              Log.e(TAG, "Error fetching stock item: ", error);
              onError.onError(new Exception(error));
            }
        );
  }

  // SHOPPING LIST OPERATIONS

  public void addShoppingListItem(ShoppingListItem item, OnSuccessListener onSuccess,
      OnErrorListener onError) {
    appDatabase.shoppingListItemDao().insertShoppingListItem(item)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Log.d(TAG, "Shopping list item added");
              onSuccess.onSuccess();
            },
            error -> {
              Log.e(TAG, "Error adding shopping list item: ", error);
              onError.onError(new Exception(error));
            }
        );
  }

  public void updateShoppingListItem(ShoppingListItem item, OnSuccessListener onSuccess,
      OnErrorListener onError) {
    appDatabase.shoppingListItemDao().updateShoppingListItem(item)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Log.d(TAG, "Shopping list item updated");
              onSuccess.onSuccess();
            },
            error -> {
              Log.e(TAG, "Error updating shopping list item: ", error);
              onError.onError(new Exception(error));
            }
        );
  }

  public void deleteShoppingListItem(int itemId, OnSuccessListener onSuccess,
      OnErrorListener onError) {
    appDatabase.shoppingListItemDao().deleteById(itemId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Log.d(TAG, "Shopping list item deleted");
              onSuccess.onSuccess();
            },
            error -> {
              Log.e(TAG, "Error deleting shopping list item: ", error);
              onError.onError(new Exception(error));
            }
        );
  }
}
