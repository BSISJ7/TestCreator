package com.BSISJ7.TestCreator.utilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.SimpleAttributeSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FillHighlighter extends DefaultHighlighter {

    private final static int MAX_STACK_SIZE = 30;

//    private final FillTextPane fillTextPane;
    private final ObservableList<FillHighlight> highlightList = FXCollections.observableArrayList();
    private final Stack<ObservableList<FillHighlight>> undoStack = new Stack<>();
    private final Stack<ObservableList<FillHighlight>> redoStack = new Stack<>();
    private boolean updatingFromRedo = false;

//    public FillHighlighter(FillTextPane fillTextPane){
//        this.fillTextPane = fillTextPane;
//        highlightList.addListener((ListChangeListener<FillHighlight>) change -> {
//            if(!updatingFromRedo && !this.fillTextPane.isSettingUp()) {
//                ObservableList<FillHighlight> newUndoList = FXCollections.observableArrayList(highlightList);
//                undoStack.push(newUndoList);
//                if(undoStack.size() > MAX_STACK_SIZE)
//                    undoStack.removeElementAt(MAX_STACK_SIZE);
//            }
//        });
//    }

//    public void setHighlight(int startOffset, int length, SimpleAttributeSet textAttributes, boolean replace) {
//        if (isDuplicateHighlight(startOffset,textAttributes) && replace && !isSameAttributeSet(startOffset, textAttributes))
//            removeHighlightAt(startOffset);
//        else if(isDuplicateHighlight(startOffset,textAttributes) && !replace)
//            return;
//        else if(isDuplicateHighlight(startOffset,textAttributes) && isSameAttributeSet(startOffset, textAttributes))
//            return;
//
//        fillTextPane.getStyledDocument().setCharacterAttributes(startOffset, length, textAttributes, replace);
//        highlightList.add(new FillHighlight(startOffset, length, textAttributes));
//    }

//    public boolean removeHighlightAt(int startOffset){
//        for (FillHighlight fillHighlight : highlightList) {
//            if (fillHighlight.startOffset == startOffset) {
//                fillTextPane.getStyledDocument().setCharacterAttributes(startOffset, fillHighlight.length,
//                        new SimpleAttributeSet(), true);
//                highlightList.remove(fillHighlight);
//                return true;
//            }
//        }
//        return false;
//    }

//    public FillHighlight getHighlightAt(int startOffset){
//        for (FillHighlight fillHighlight : highlightList) {
//            if (fillHighlight.startOffset == startOffset) {
//                return fillHighlight;
//            }
//        }
//        return null;
//    }

//    public boolean isSameAttributeSet(int startOffset, SimpleAttributeSet attribute){
//        try {
//            FillHighlight highlight = getHighlightAt(startOffset);
//            return highlight.gettextAttributes().equals(attribute);
//        }catch(NullPointerException e){
//            e.printStackTrace();
//            return false;
//        }
//    }


//    public void clearHighlights(){
//        for(int x = 0; x < highlightList.size(); x++){
//            fillTextPane.getStyledDocument().setCharacterAttributes(highlightList.get(x).startOffset, highlightList.get(x).length,
//                    new SimpleAttributeSet(), true);
//            highlightList.remove(x);
//        }
//    }

//    public int getCurrentHighlightOffset(){
//        return highlightList.get(highlightList.size()-1).startOffset;
//    }

//    public void replaceLastHighlight(SimpleAttributeSet textAttributes){
//        if(highlightList.size() > 0) {
//            FillHighlight previousHighlight = highlightList.get(highlightList.size()-1);
//            int prevStartOffset = previousHighlight.startOffset;
//            int prevLength = previousHighlight.length;
//            setHighlight(prevStartOffset, prevLength, textAttributes, true);
//
//        }
//    }

    public List<FillHighlight> getHighlightList(){
        List<FillHighlight> highlightLocations = new ArrayList<>();
        highlightList.forEach(highlight -> {
            highlightLocations.add(highlight);
        });

        return highlightList;
    }

//    /**
//     *Removes the last highlight only if it equals the textAttributes parameter.
//     * @param compareAttribute Attribute which is compared to the currently set attribute.
//     */
//    public void removeLastHighlight(SimpleAttributeSet compareAttribute){
//        if(highlightList.size() > 0) {
//            FillHighlight previousHighlight = highlightList.get(highlightList.size()-1);
//            if(previousHighlight.gettextAttributes().equals(compareAttribute)) {
//                int prevStartOffset = previousHighlight.startOffset;
//                int prevLength = previousHighlight.length;
////                System.out.println("TexT: "+fillTextPane.getText().substring(prevStartOffset, prevStartOffset+prevLength));
//                fillTextPane.getStyledDocument().setCharacterAttributes(prevStartOffset, prevLength, new SimpleAttributeSet(), true);
//                highlightList.remove(previousHighlight);
//            }
//        }
//    }

//    void undoHighlights(){
//        if(!undoStack.empty()) {
//            clearHighlights();
//            ObservableList<FillHighlight> undoHighlightsList = undoStack.pop();
//            redoStack.push(undoHighlightsList);
//            FXCollections.copy(highlightList, undoHighlightsList);
//            if(redoStack.size() > MAX_STACK_SIZE)
//                redoStack.removeElementAt(MAX_STACK_SIZE);
//        }
//    }

    private boolean isDuplicateHighlight(int startOffset, SimpleAttributeSet textAttributes){
        for (FillHighlight fillHighlight : highlightList) {
            if(fillHighlight.startOffset == startOffset)
                return true;
        }

        return false;
    }

    private void adjustHighlightOffsets(int offset, int increment){
        highlightList.forEach(fillHighlight -> {
            if(fillHighlight.startOffset >= offset)
                fillHighlight.startOffset += increment;
        });
    }

    public class FillHighlight implements Comparable{

        private int startOffset = 0;
        private int endOffset = 0;
        private int length = 0;
        private SimpleAttributeSet textAttributes;

        private FillHighlight(int startOffset, int length, SimpleAttributeSet textAttributes) {
            this.startOffset = startOffset;
            this.length = length;
            this.textAttributes = textAttributes;
            this.endOffset = startOffset + length;
        }

        private FillHighlight getCopy(){
            return new FillHighlight(startOffset, length, textAttributes);
        }

        public int getStartOffset() {
            return startOffset;
        }

        private void setStartOffset(int startOffset) {
            this.startOffset = startOffset;
        }

        public int getLength() {
            return length;
        }

        private void setLength(int length) {
            this.length = length;
        }

        private SimpleAttributeSet gettextAttributes() {
            return textAttributes;
        }

        private void setTextAttributes(SimpleAttributeSet textAttributes) {
            this.textAttributes = textAttributes;
        }

        public int getEndOffset() {
            return endOffset;
        }

        private void setEndOffset(int endOffset) {
            this.endOffset = endOffset;
        }

        @Override
        public int hashCode() {
            return Integer.parseInt(""+startOffset+length+endOffset);
        }

        @Override
        public int compareTo(Object obj) {
            FillHighlight highlight = (FillHighlight)obj;
            if(obj == null)
                return -1;

            if(highlight == this)
                return 0;

            if(this.hashCode() > highlight.hashCode())
                return 1;
            else if(this.hashCode() < highlight.hashCode())
                return -1;
            else
                return 0;
        }
    }
}
