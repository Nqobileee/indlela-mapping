package com.trackify.app.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LocationDao_Impl implements LocationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SavedLocation> __insertionAdapterOfSavedLocation;

  private final EntityDeletionOrUpdateAdapter<SavedLocation> __deletionAdapterOfSavedLocation;

  public LocationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSavedLocation = new EntityInsertionAdapter<SavedLocation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `saved_locations` (`id`,`name`,`latitude`,`longitude`,`radius`,`geofence_id`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final SavedLocation entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        statement.bindDouble(3, entity.latitude);
        statement.bindDouble(4, entity.longitude);
        statement.bindDouble(5, entity.radius);
        if (entity.geofenceId == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.geofenceId);
        }
      }
    };
    this.__deletionAdapterOfSavedLocation = new EntityDeletionOrUpdateAdapter<SavedLocation>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `saved_locations` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final SavedLocation entity) {
        statement.bindLong(1, entity.id);
      }
    };
  }

  @Override
  public long insert(final SavedLocation location) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSavedLocation.insertAndReturnId(location);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final SavedLocation location) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfSavedLocation.handle(location);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<SavedLocation> getAll() {
    final String _sql = "SELECT * FROM saved_locations ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
      final int _cursorIndexOfGeofenceId = CursorUtil.getColumnIndexOrThrow(_cursor, "geofence_id");
      final List<SavedLocation> _result = new ArrayList<SavedLocation>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final SavedLocation _item;
        _item = new SavedLocation();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        _item.radius = _cursor.getFloat(_cursorIndexOfRadius);
        if (_cursor.isNull(_cursorIndexOfGeofenceId)) {
          _item.geofenceId = null;
        } else {
          _item.geofenceId = _cursor.getString(_cursorIndexOfGeofenceId);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<SavedLocation>> getAllLive() {
    final String _sql = "SELECT * FROM saved_locations ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"saved_locations"}, false, new Callable<List<SavedLocation>>() {
      @Override
      @Nullable
      public List<SavedLocation> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRadius = CursorUtil.getColumnIndexOrThrow(_cursor, "radius");
          final int _cursorIndexOfGeofenceId = CursorUtil.getColumnIndexOrThrow(_cursor, "geofence_id");
          final List<SavedLocation> _result = new ArrayList<SavedLocation>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedLocation _item;
            _item = new SavedLocation();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfName)) {
              _item.name = null;
            } else {
              _item.name = _cursor.getString(_cursorIndexOfName);
            }
            _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
            _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
            _item.radius = _cursor.getFloat(_cursorIndexOfRadius);
            if (_cursor.isNull(_cursorIndexOfGeofenceId)) {
              _item.geofenceId = null;
            } else {
              _item.geofenceId = _cursor.getString(_cursorIndexOfGeofenceId);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
