# Scheduler
Scheduler 는 다양한 **Event** 를 효과적으로 처리 할 수 있는 Library 이다.

![Event Dispatcher](https://github.com/orosys/scheduler/blob/master/event_dispatcher.png?raw=true)
* **Event** : Click, Alarm, System event 등 다양한 이벤트를 구성할 수 있다.
* **Event Dispatcher** : 발생된 Event 를 등록된 Actor 들에게 전달 한다.
* **Actor** : Event 로 부터 실행되는데 모든 Event 를 받을 수 있고, 필요에 따라 선택적으로 수행 가능하다.

# Using Scheduler
* AndroidManifest.xml 에 Service 등록
  ```
  <service android:name="com.oro.scheduler.EventDispatcherServiceImpl"/>
  ```
  >EventDispatcherServiceImpl 를 상속한 Class 적용 가능함.

* Event 생성
    ```
    public class LoopEvent extends BroadcastReceiver implements IEvent {
      private static final String TAG = LoopEvent.class.getSimpleName();
      private ActionEvent action = new ActionEvent("loop", new String[]{"loop_event"});
      private SchedulingAlarmReceiver loopReceiver;

      @Override
      public void onReceive(Context context, Intent intent) {
          AbstractEventDispatcherService.dispatchAction(context, this);
      }

      @Override
      public ActionEvent getActionEvent() {
          return action;
      }

      @Override
      public void startEvent(Context context) {
          // 60 second loop
          loopReceiver = new SchedulingAlarmReceiver(context, LoopEvent.class);
          loopReceiver.setAlarmRepeatingScheduling(context, 60 * 1000, true);
      }

      @Override
      public void stopEvent(Context context) {
          loopReceiver = new SchedulingAlarmReceiver(context, LoopEvent.class);
          loopReceiver.setAlarmRepeatingScheduling(context, 0, false);
      }
    }
    ```
    >60초 주기로 Event 를 발생시키는 예제이다. 
    >Event 가 생성되면 *startEvent* 가 실행되어 Event 구동에 필요한 처리를 하고, *AbstractEventDispatcherService.dispatchAction* API 를 사용하여 Event 를 발생시킨다.
    >**startEvent** : Event 시작될때 Event Dispatcher 로 부터 호출됨
    >**stopEvent** : Event 정지될때 Event Dispatcher 로 부터 호출됨

* Actor 생성
    ```
    public class EventActor extends AbstractActionTask {
        @Override
        public void onReceiveActionEvent(Context context, ActionEvent actionEvent) {
            Log.i("EventActor", "Trigger by " + actionEvent.getEventType());
        }
    }
    ```
    >Event DispatcherService 로 부터 Event 를 전달 받아 원하는 작업을 수행 한다. 특정 이벤트만 처리할 경우 *actionEvent.getEventType()* 를 사용하여 분기 처리하면 된다.
* Event, Actor 등록
    ```
    AbstractEventDispatcherService.addEvent(this, LoopEvent.class);
    AbstractEventDispatcherService.addAction(this, EventActor.class);

    AbstractEventDispatcherService.startEvent(this);
    ```
    >**addEvent** : 사용할 Event 를 등록
    >**addAction** : 사용할 Actor 를 등록
    >**startEvent** : Event 를 초기화 (*Event.startEvent* 구동)
