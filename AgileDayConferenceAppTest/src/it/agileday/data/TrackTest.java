package it.agileday.data;

import it.agileday.utils.Dates;
import it.aglieday.data.Session;
import it.aglieday.data.Track;

import java.util.Date;

import junit.framework.TestCase;

public class TrackTest extends TestCase {
	private Date d1 = Dates.newDate(2010, 1, 1, 10);
	private Date d2 = Dates.newDate(2010, 1, 1, 11);
	private Date d3 = Dates.newDate(2010, 1, 1, 12);
	private Date d4 = Dates.newDate(2010, 1, 1, 13);

	public void setUp() {
	}

	public void test_track_with_invalid_session_is_invalid() {
		Track target = new Track()
				.addSession(new Session().setId(1).setStart(d2).setEnd(d1));
		assertEquals("Session#1: End date must be greater than start date", target.validationMessage());
	}

	public void test_track_with_overlapping_session_is_invalid() {
		Track target = new Track()
				.addSession(new Session().setStart(d1).setEnd(d3))
				.addSession(new Session().setStart(d2).setEnd(d4));
		assertEquals("Session overlap/bad order detected", target.validationMessage());
	}

	public void test_valid_track_cases() {
		Track target = new Track()
				.addSession(new Session().setStart(d1).setEnd(d2))
				.addSession(new Session().setStart(d2).setEnd(d3));
		assertTrue(target.isValid());
	}
}