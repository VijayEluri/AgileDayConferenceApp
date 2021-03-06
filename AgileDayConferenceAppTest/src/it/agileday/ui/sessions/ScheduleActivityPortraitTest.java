/*
	Copyright 2010 
	
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

import it.agileday.test.utils.ActivityInstrumentationOrientedScreenTestCase;
import android.content.res.Configuration;

public class ScheduleActivityPortraitTest extends ActivityInstrumentationOrientedScreenTestCase<ScheduleActivity> {

	private ScheduleActivity scheduleActivity;

	public ScheduleActivityPortraitTest() {
		super("it.agileday.ui.sessions", ScheduleActivity.class, Configuration.ORIENTATION_PORTRAIT);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		scheduleActivity = this.getActivity();
	}

	public void test_a_oncreate_activity() {
		assertNotNull(scheduleActivity);
	}

}
