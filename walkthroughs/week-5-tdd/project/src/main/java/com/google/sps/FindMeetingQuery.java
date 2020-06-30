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
    ArrayList<Event> eventsList = new ArrayList<Event>(events);
    long meetingDuration = request.getDuration();

    if (meetingDuration > TimeRange.WHOLE_DAY.duration()) {
      return spots;
    }
    
    if (eventsList.isEmpty() || request.getAttendees().isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    /** Deletes irrelevant events. */
    eventsList.removeIf(event -> Collections.disjoint(request.getAttendees(), 
                                                      event.getAttendees()));

    boolean sorted = true;
    do {
      int currentEvent = 0;

      /** Loops through the events list. */
      while (currentEvent < eventsList.size() - 1) {

        /** Sorts the events list by start time. */
        if (TimeRange.ORDER_BY_START.compare(eventsList.get(currentEvent).getWhen(), 
              eventsList.get(currentEvent+1).getWhen()) > 0) {
          Event tmp = eventsList.get(currentEvent);
          eventsList.set(currentEvent, eventsList.get(currentEvent+1));
          eventsList.set(currentEvent+1, tmp);
          sorted = false;
          currentEvent++;
        }

        /** Deletes the next event if contained by the current event. */
        else if (TimeRange.ORDER_BY_END.compare(eventsList.get(currentEvent).getWhen(), 
              eventsList.get(currentEvent+1).getWhen()) > 0) {
        eventsList.remove(currentEvent+1);
        }

        else {
          currentEvent++;
        }
      }
    } while (!sorted);
    
    int start = TimeRange.START_OF_DAY;
    for (int i = 0; i < eventsList.size(); i++) {
      int end = eventsList.get(i).getWhen().start();
      int spotDuration = end - start;
      if (spotDuration >= meetingDuration) {
        spots.add(TimeRange.fromStartEnd(start, end, false));
      }
      start = eventsList.get(i).getWhen().end();
    }
    // From the end of the last event to the end of day
    if (TimeRange.END_OF_DAY - start >= meetingDuration) {
      spots.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
    }

    return spots;
  }
}
