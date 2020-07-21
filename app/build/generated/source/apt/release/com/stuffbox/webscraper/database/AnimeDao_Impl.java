package com.stuffbox.webscraper.database;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.stuffbox.webscraper.models.Anime;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AnimeDao_Impl implements AnimeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Anime> __insertionAdapterOfAnime;

  private final EntityDeletionOrUpdateAdapter<Anime> __deletionAdapterOfAnime;

  private final EntityDeletionOrUpdateAdapter<Anime> __updateAdapterOfAnime;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAnimeByNameAndEpisodeNo;

  public AnimeDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAnime = new EntityInsertionAdapter<Anime>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `anime` (`id`,`name`,`link`,`episodeNo`,`imageLink`,`time`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Anime value) {
        stmt.bindLong(1, value.id);
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getLink() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getLink());
        }
        if (value.getEpisodeNo() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getEpisodeNo());
        }
        if (value.getImageLink() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getImageLink());
        }
        if (value.getTime() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getTime());
        }
      }
    };
    this.__deletionAdapterOfAnime = new EntityDeletionOrUpdateAdapter<Anime>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `anime` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Anime value) {
        stmt.bindLong(1, value.id);
      }
    };
    this.__updateAdapterOfAnime = new EntityDeletionOrUpdateAdapter<Anime>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `anime` SET `id` = ?,`name` = ?,`link` = ?,`episodeNo` = ?,`imageLink` = ?,`time` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Anime value) {
        stmt.bindLong(1, value.id);
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getLink() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getLink());
        }
        if (value.getEpisodeNo() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getEpisodeNo());
        }
        if (value.getImageLink() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getImageLink());
        }
        if (value.getTime() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getTime());
        }
        stmt.bindLong(7, value.id);
      }
    };
    this.__preparedStmtOfDeleteAnimeByNameAndEpisodeNo = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM anime WHERE episodeNo = ? and name= ?";
        return _query;
      }
    };
  }

  @Override
  public void insertAnime(final Anime anime) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfAnime.insert(anime);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAnime(final Anime anime) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfAnime.handle(anime);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateAnime(final Anime anime) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfAnime.handle(anime);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAnimeByNameAndEpisodeNo(final String name, final String episodeNo) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAnimeByNameAndEpisodeNo.acquire();
    int _argIndex = 1;
    if (episodeNo == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, episodeNo);
    }
    _argIndex = 2;
    if (name == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, name);
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAnimeByNameAndEpisodeNo.release(_stmt);
    }
  }

  @Override
  public List<Anime> getAnimeList() {
    final String _sql = "Select * from anime order by id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfLink = CursorUtil.getColumnIndexOrThrow(_cursor, "link");
      final int _cursorIndexOfEpisodeNo = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeNo");
      final int _cursorIndexOfImageLink = CursorUtil.getColumnIndexOrThrow(_cursor, "imageLink");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final List<Anime> _result = new ArrayList<Anime>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Anime _item;
        _item = new Anime();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        _item.setName(_tmpName);
        final String _tmpLink;
        _tmpLink = _cursor.getString(_cursorIndexOfLink);
        _item.setLink(_tmpLink);
        final String _tmpEpisodeNo;
        _tmpEpisodeNo = _cursor.getString(_cursorIndexOfEpisodeNo);
        _item.setEpisodeNo(_tmpEpisodeNo);
        final String _tmpImageLink;
        _tmpImageLink = _cursor.getString(_cursorIndexOfImageLink);
        _item.setImageLink(_tmpImageLink);
        final String _tmpTime;
        _tmpTime = _cursor.getString(_cursorIndexOfTime);
        _item.setTime(_tmpTime);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Anime getAnimeByNameAndEpisodeNo(final String name, final String episodeNo) {
    final String _sql = "SELECT * FROM anime where name = ? and episodeNo= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (name == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, name);
    }
    _argIndex = 2;
    if (episodeNo == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeNo);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfLink = CursorUtil.getColumnIndexOrThrow(_cursor, "link");
      final int _cursorIndexOfEpisodeNo = CursorUtil.getColumnIndexOrThrow(_cursor, "episodeNo");
      final int _cursorIndexOfImageLink = CursorUtil.getColumnIndexOrThrow(_cursor, "imageLink");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final Anime _result;
      if(_cursor.moveToFirst()) {
        _result = new Anime();
        _result.id = _cursor.getInt(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        _result.setName(_tmpName);
        final String _tmpLink;
        _tmpLink = _cursor.getString(_cursorIndexOfLink);
        _result.setLink(_tmpLink);
        final String _tmpEpisodeNo;
        _tmpEpisodeNo = _cursor.getString(_cursorIndexOfEpisodeNo);
        _result.setEpisodeNo(_tmpEpisodeNo);
        final String _tmpImageLink;
        _tmpImageLink = _cursor.getString(_cursorIndexOfImageLink);
        _result.setImageLink(_tmpImageLink);
        final String _tmpTime;
        _tmpTime = _cursor.getString(_cursorIndexOfTime);
        _result.setTime(_tmpTime);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
