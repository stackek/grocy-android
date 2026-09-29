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

package xyz.zedler.patrick.grocy.database;

import android.util.Log;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import xyz.zedler.patrick.grocy.model.Location;
import xyz.zedler.patrick.grocy.model.ProductGroup;
import xyz.zedler.patrick.grocy.model.QuantityUnit;
import xyz.zedler.patrick.grocy.model.Store;

public class DatabaseInitializer {

  private static final String TAG = "DatabaseInitializer";

  /**
   * Bootstrap the local database with default data if empty.
   * This ensures the app works offline without any server connection.
   */
  public static void initializeIfEmpty(AppDatabase db) {
    db.quantityUnitDao().getAll()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            units -> {
              if (units.isEmpty()) {
                Log.d(TAG, "Database is empty. Initializing with default data...");
                seedDefaultData(db);
              } else {
                Log.d(TAG, "Database already contains data. Skipping initialization.");
              }
            },
            error -> Log.e(TAG, "Error checking database: ", error)
        );
  }

  private static void seedDefaultData(AppDatabase db) {
    // Create default quantity units
    List<QuantityUnit> units = new ArrayList<>();
    units.add(new QuantityUnit(1, "kg", "Kilogram"));
    units.add(new QuantityUnit(2, "g", "Gram"));
    units.add(new QuantityUnit(3, "l", "Liter"));
    units.add(new QuantityUnit(4, "ml", "Milliliter"));
    units.add(new QuantityUnit(5, "piece", "Piece"));
    units.add(new QuantityUnit(6, "cup", "Cup"));
    units.add(new QuantityUnit(7, "tbsp", "Tablespoon"));
    units.add(new QuantityUnit(8, "tsp", "Teaspoon"));

    // Create default locations
    List<Location> locations = new ArrayList<>();
    locations.add(new Location(1, "Fridge", "Cold storage"));
    locations.add(new Location(2, "Pantry", "Dry storage"));
    locations.add(new Location(3, "Freezer", "Frozen storage"));
    locations.add(new Location(4, "Cupboard", "Kitchen cupboard"));

    // Create default product groups
    List<ProductGroup> groups = new ArrayList<>();
    groups.add(new ProductGroup(1, "Vegetables", null));
    groups.add(new ProductGroup(2, "Fruits", null));
    groups.add(new ProductGroup(3, "Dairy", null));
    groups.add(new ProductGroup(4, "Meat", null));
    groups.add(new ProductGroup(5, "Pantry Staples", null));
    groups.add(new ProductGroup(6, "Beverages", null));

    // Create default stores
    List<Store> stores = new ArrayList<>();
    stores.add(new Store(1, "Home"));
    stores.add(new Store(2, "Grocery Store"));
    stores.add(new Store(3, "Market"));

    // Insert all default data
    db.quantityUnitDao().insertQuantityUnits(units)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnComplete(() -> db.locationDao().insertLocations(locations)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete(() -> db.productGroupDao().insertProductGroups(groups)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> db.storeDao().insertStores(stores)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        () -> Log.d(TAG, "Database initialized successfully with default data"),
                        error -> Log.e(TAG, "Error initializing stores: ", error)
                    )
                )
                .subscribe(
                    () -> {},
                    error -> Log.e(TAG, "Error initializing product groups: ", error)
                )
            )
            .subscribe(
                () -> {},
                error -> Log.e(TAG, "Error initializing locations: ", error)
            )
        )
        .subscribe(
            () -> {},
            error -> Log.e(TAG, "Error initializing quantity units: ", error)
        );
  }
}
