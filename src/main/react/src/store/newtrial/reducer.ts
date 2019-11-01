import * as R from "ramda";
import {Scale} from "../../pages/Scale";
import {
    EXCLUDE_FROM_TRIAL,
    ExcludeFromTrialAction,
    INCLUDE_IN_TRIAL,
    IncludeInTrialAction,
    NewTrialActionTypes,
    NewTrialState,
    UPDATE_TRIAL_NAME, UpdateTrialNameAction,
} from "./types";

const initialState: NewTrialState = {
    trialName: "",
    scales: [],
};

export function newTrialReducer(
    state = initialState,
    action: NewTrialActionTypes,
): NewTrialState {
    switch (action.type) {
        case INCLUDE_IN_TRIAL:
            return {
                ...state,
                scales: R.append((action as IncludeInTrialAction).scale, state.scales),
            };
        case EXCLUDE_FROM_TRIAL:
            return {
                ...state,
                scales: R.filter((scale: Scale) => scale !== (action as ExcludeFromTrialAction).scale, state.scales),
            };
        case UPDATE_TRIAL_NAME:
            return {
                ...state,
                trialName: (action as UpdateTrialNameAction).name,
            };
        default:
            return state;
    }
}
