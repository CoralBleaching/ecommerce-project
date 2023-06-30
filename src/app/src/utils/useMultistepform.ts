import { ReactElement, useState } from "react";

type Step = {
    step: ReactElement
    skippable: boolean
}

export default function useMultiStepForm(steps: Step[]) {
    const [currentStepIndex, setCurrentStepIndex] = useState(0)
    function onNext() {
        setCurrentStepIndex(i => {
            if (i >= steps.length - 1) {
                return i 
            }
            return i + 1
        })
    }

    function onBack() {
        setCurrentStepIndex(i => {
            if (i <= 0) {
                return i 
            }
            return i - 1
        })
    }

    function goTo(index: number) {
        if (index <= steps.length && steps.length >= 0) {
            setCurrentStepIndex(index)
        }
    }

    return {
        currentStepIndex,
        totalSteps: steps.length,
        isFirstStep: currentStepIndex === 0,
        isLastStep: currentStepIndex >= steps.length - 1,
        isSkippable: steps[currentStepIndex].skippable,
        step: steps[currentStepIndex].step,
        goTo,
        onNext,
        onBack,
    }
}