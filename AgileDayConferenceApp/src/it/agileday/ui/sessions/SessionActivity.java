/*
	Copyright 2010 
	
	Author: Gian Marco Gherardi
	Author: Ilias Bartolini

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package it.agileday.ui.sessions;

import it.agileday.R;
import it.agileday.data.DatabaseHelper;
import it.agileday.data.Session;
import it.agileday.data.Track;
import it.agileday.data.TrackRepository;
import it.agileday.utils.Dates;
import it.agileday.utils.FlingViewGestureListener;
import it.agileday.utils.HookableViewAnimator;

import java.util.ArrayList;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class SessionActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = SessionActivity.class.getName();
	private static final String TIME_FORMAT = "HH:mm";
	private static final float DIP_PER_MINUTE = 2.0f;

	private HookableViewAnimator viewAnimator;
	private GestureDetector gestureDetector;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sessions);
		viewAnimator = (HookableViewAnimator) findViewById(R.id.flipper);
		viewAnimator.setOnChildDisplayingListener(new OnChildDisplayingListener());
		gestureDetector = new GestureDetector(this, new FlingViewGestureListener(viewAnimator));
		fillData();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return gestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
	}

	public void onLeftArrowClick(View v) {
		viewAnimator.goToLeft();
	}

	public void onRightArrowClick(View v) {
		viewAnimator.goToRight();
	}

	private void fillData() {
		SQLiteDatabase database = new DatabaseHelper(this).getReadableDatabase();
		try {
			ArrayList<Track> tracks = new TrackRepository(database, this).getAll();
			int i = 0;
			for (Track track : tracks) {
				if (!track.isValid()) {
					throw new RuntimeException(track.validationMessage());
				}
				View view = buildView(R.layout.track, viewAnimator);
				setText(view, R.id.title, track.title);
				updateLeftRightTrackButton(view, R.id.previous_session, i);
				updateLeftRightTrackButton(view, R.id.next_session, tracks.size() - (i + 1));

				ViewGroup sessionsViewGroup = (ViewGroup) view.findViewById(R.id.sessions);
				for (Session session : track.getSessions()) {
					sessionsViewGroup.addView(getSessionView(sessionsViewGroup, session, !track.hasNext(session)));
					if (track.hasNext(session)) {
						sessionsViewGroup.addView(buildView(R.layout.session_item_separator, sessionsViewGroup));
					}
				}
				viewAnimator.addView(view);
				i++;
			}
		} finally {
			database.close();
		}
	}

	private void updateLeftRightTrackButton(View view, int button_id, int howManyRamining) {
		if (howManyRamining > 0) {
			String buttonText = "";
			for (int j = 0; j < howManyRamining; j++)
				buttonText = buttonText.concat("o");
			((Button) view.findViewById(button_id)).setText(buttonText);
		} else {
			((Button) view.findViewById(button_id)).setVisibility(View.INVISIBLE);
		}
	}

	public View getSessionView(ViewGroup parent, Session session, boolean last) {
		View view = buildView(R.layout.sessions_item, parent);
		setSessionViewHeight(view, session);
		setText(view, R.id.title, session.title);
		setText(view, R.id.start, Dates.toString(session.getStart(), TIME_FORMAT));
		setText(view, R.id.end, (last ? Dates.toString(session.getEnd(), TIME_FORMAT) : ""));
		return view;
	}

	private void setSessionViewHeight(View view, Session session) {
		float scale = getResources().getDisplayMetrics().density;
		int height = (int) (Dates.differenceMinutes(session.getEnd(), session.getStart()) * scale * DIP_PER_MINUTE);
		view.getLayoutParams().height = height;
	}

	private void setText(View view, int id, String text) {
		TextView txt = (TextView) view.findViewById(id);
		txt.setText(text);
	}

	private View buildView(int id, ViewGroup parent) {
		return getLayoutInflater().inflate(id, parent, false);
	}

	private class OnChildDisplayingListener implements HookableViewAnimator.OnChildDisplayingListener {
		@Override
		public boolean onChildDisplayedChanging(HookableViewAnimator sender, View oldChild, View newChild) {
			return true;
		}

		@Override
		public void onChildDisplayedChanged(HookableViewAnimator sender, View oldChild, View newChild) {
			final ScrollView oldView = (ScrollView) oldChild.findViewById(R.id.scroll);
			final ScrollView newView = (ScrollView) newChild.findViewById(R.id.scroll);
			newView.post(new Runnable() {
				@Override
				public void run() {
					newView.scrollTo(newView.getScrollX(), oldView.getScrollY());
				}
			});
		}
	}
}
