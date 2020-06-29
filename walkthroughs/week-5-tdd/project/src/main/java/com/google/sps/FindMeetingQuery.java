// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> spots = new ArrayList<TimeRange>();
    ArrayList<String> attendees = new ArrayList<String>(request.getAttendees());
    ArrayList<Event> eventsList = new ArrayList<Event>(events);
    long duration = request.getDuration();
    if (duration > TimeRange.WHOLE_DAY.duration()) {
      return spots;
    }
    
    if (eventsList.isEmpty() || attendees.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    /** Sorts the events list by start time. */
    boolean sorted = true;
    do {
      for (int i = 0; i < eventsList.size() - 1; i++) {
        if (TimeRange.ORDER_BY_START
            .compare(eventsList.get(i).getWhen(), eventsList.get(i+1).getWhen()) > 0) {
          Event tmp = eventsList.get(i);
          eventsList.set(i, eventsList.get(i+1));
          eventsList.set(i+1, tmp);
          sorted = false;
        }
      }
    } while (!sorted);

    /** Deletes irrelevant events and events contained by other events. */
    for (int i = 0; i < eventsList.size(); i++) {
      boolean relevant = false;
      int j = 0;
      while (j < attendees.size() && (!relevant)) {
        if (eventsList.get(i).getAttendees().contains(attendees.get(j))) {
          relevant = true;
        }
        j++;
      }
      if (!relevant) {
        eventsList.remove(i);
        i--;
      } else if ((i < eventsList.size() - 1) && 
                 (eventsList.get(i).getWhen().contains(eventsList.get(i+1).getWhen()))) {
        eventsList.remove(i+1);
        i--;
      }
    }

    if (eventsList.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    int start = TimeRange.START_OF_DAY;
    int end = eventsList.get(0).getWhen().start();
    int spotDuration = end - start;
    for (int i = 0; i < eventsList.size(); i++) {
      if (spotDuration >= duration) {
        spots.add(TimeRange.fromStartEnd(start, end, false));
      }
      start = eventsList.get(i).getWhen().end();
      end = (i == eventsList.size() - 1) ? TimeRange.END_OF_DAY : eventsList.get(i+1).getWhen().start();
      spotDuration = end - start;
    }
    // From the end of the last event to the end of day
    if (spotDuration >= duration) {
      spots.add(TimeRange.fromStartEnd(start, end, true));
    }

    return spots;
  }
}
