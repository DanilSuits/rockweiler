package rockweiler;

import rockweiler.draft.Draft;
import rockweiler.draft.DraftListener;
import rockweiler.draft.DraftService;
import rockweiler.draft.Slot;
import rockweiler.draft.TrivialDraftService;

/**
 * Hello world!
 */
public class LocalDraftApp implements Demo {
    public static void main(String[] args) {
        Draft<String> theDraft = SimpleDraftScript.createTrivialDraft();

        LocalDraftApp theApp = new LocalDraftApp(theDraft);

        SimpleDraftScript.run(theApp);
    }

    private final Draft<String> theDraft;
    private int nextPick;

    public LocalDraftApp(Draft<String> theDraft) {
        this.theDraft = theDraft;
        nextPick = theDraft.onClock();
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
        int pickId = --nextPick;
        Slot<String> undo = new Slot<String>(pickId,null,Slot.BasicStates.PENDING,null);
        theDraft.update(undo);
    }

    public void skip() {
        int pickId = nextPick++;
        Slot<String> skip = new Slot<String>(pickId,null,Slot.BasicStates.SKIPPED,null);
        theDraft.update(skip);
    }

    public void draft(String name) {
        int pickId = nextPick++;
        Slot<String> pick = new Slot<String>(pickId,null,Slot.BasicStates.USED,name);
        theDraft.update(pick);
    }
}
