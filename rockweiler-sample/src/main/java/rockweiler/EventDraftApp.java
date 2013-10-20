package rockweiler;

import rockweiler.draft.Draft;
import rockweiler.draft.DraftListener;
import rockweiler.draft.DraftService;
import rockweiler.draft.Slot;
import rockweiler.draft.TrivialDraftService;

/**
 * Hello world!
 */
public class EventDraftApp implements Demo, DraftListener<String> {
    public static void main(String[] args) {
        Draft<String> theDraft = SimpleDraftScript.createTrivialDraft();
        TrivialDraftService<String> draftService = new TrivialDraftService<String>(theDraft);

        EventDraftApp theApp = new EventDraftApp(draftService);
        draftService.subscribe(theApp);

        SimpleDraftScript.run(theApp);
    }

    private final DraftService<String> draftService;
    private Draft<String> theDraft;
    private int lastPick = -1;

    public EventDraftApp(DraftService<String> draftService) {
        this.draftService = draftService;
    }

    public void show() {
        StringBuffer report = new StringBuffer();

        for(Slot<String> s : theDraft.copy()) {
            report.append(s.pickId);
            report.append("  ");
            if (Slot.BasicStates.USED.equals(s.state)) {
                report.append(s.player);
            } else {
                report.append(s.state);
            }
            report.append('\n');
        }

        System.out.println(report.toString());
    }

    public void undo() {
        int pickId = --lastPick;
        Slot<String> undo = new Slot<String>(pickId,null,Slot.BasicStates.PENDING,null);
        draftService.requestChange(undo);
    }

    public void skip() {
        int pickId = lastPick++;
        Slot<String> skip = new Slot<String>(pickId,null,Slot.BasicStates.SKIPPED,null);
        draftService.requestChange(skip);
    }

    public void draft(String name) {
        int pickId = lastPick++;
        Slot<String> pick = new Slot<String>(pickId,null,Slot.BasicStates.USED,name);
        draftService.requestChange(pick);
    }

    public void onLoad(Draft<String> crntDraft) {
        theDraft = crntDraft;
        lastPick = theDraft.onClock();
    }

    public void onChange(Slot<? extends String> slot) {
        theDraft.update(slot);
    }
}
