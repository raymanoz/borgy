import * as R from "ramda";
import {Scale} from "../../pages/Scale";
import {
    CLEAR_NEW_TRIAL,
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
        case INCLUDE_IN_TRIAL: {
            const includeAction = action as IncludeInTrialAction;
            return {
                ...state,
                scales: R.uniq(R.append(includeAction.scale, state.scales)),
            };
        }
        case EXCLUDE_FROM_TRIAL:
            const excludeAction = action as ExcludeFromTrialAction;
            return {
                ...state,
                scales: R.filter((scale: Scale) => scale.name !== excludeAction.scale.name, state.scales),
            };
        case UPDATE_TRIAL_NAME:
            const updateAction = action as UpdateTrialNameAction;
            return {
                ...state,
                trialName: updateAction.name,
            };
        case CLEAR_NEW_TRIAL:
            return initialState;
        default:
            return state;
    }
}
