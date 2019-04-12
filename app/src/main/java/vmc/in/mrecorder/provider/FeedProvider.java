/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vmc.in.mrecorder.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;


public class FeedProvider extends ContentProvider {



    public static final String CONTENT_AUTHORITY = "vmc.in.mrecorder";
    private static final String AUTHORITY = CONTENT_AUTHORITY;
    private static final String PATH_ENTRIES = "entries";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.basicsyncadapter.entries";
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.basicsyncadapter.entry";

    public static final int ROUTE_ENTRIES = 1;
    public static final int ROUTE_ENTRIES_ID = 2;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID);
    }

    @Override
    public boolean onCreate() {

        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES:
                return CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
            case ROUTE_ENTRIES:

                Context ctx = getContext();
                assert ctx != null;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        return 0;
    }

}
