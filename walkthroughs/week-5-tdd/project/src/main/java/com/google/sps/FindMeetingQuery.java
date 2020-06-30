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

import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> spots = new ArrayList<TimeRange>();
    ArrayList<Event> eventsList = new ArrayList<Event>(events);
    long meetingDuration = request.getDuration();

    if (meetingDuration > TimeRange.WHOLE_DAY.duration()) {
      return spots;
    }

    /** Deletes irrelevant events. */
    eventsList.removeIf(event -> Collections.disjoint(request.getAttendees(), 
                                                      event.getAttendees()));

    if (eventsList.isEmpty() || request.getAttendees().isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    /** Sorts the events list by start time. */
    Collections.sort(eventsList, new Comparator<Event>(){
      public int compare(Event a, Event b) {
        return TimeRange.ORDER_BY_START.compare(a.getWhen(), b.getWhen());
      }
    });
    
    int start = TimeRange.START_OF_DAY;
    for (int currentEvent = 0; currentEvent < eventsList.size(); currentEvent++) {
      int end = eventsList.get(currentEvent).getWhen().start();
      if (end - start >= meetingDuration) {
        spots.add(TimeRange.fromStartEnd(start, end, false));
      }
      start = Math.max(start, eventsList.get(currentEvent).getWhen().end());
    }
    // From the end of the last event to the end of day
    if (TimeRange.END_OF_DAY - start >= meetingDuration) {
      spots.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    }

    return spots;
  }
}
