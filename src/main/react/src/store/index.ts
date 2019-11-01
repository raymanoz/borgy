import {applyMiddleware, combineReducers, createStore} from "redux";
import {composeWithDevTools} from "redux-devtools-extension";
import thunkMiddleware from "redux-thunk";
import {activeTrialsReducer} from "./activetrials/reducer";
import {ActiveTrialsState} from "./activetrials/types";
import {newTrialReducer} from "./newtrial/reducer";
import {NewTrialState} from "./newtrial/types";
import {scalesReducer} from "./scales/reducers";
import {ScalesState} from "./scales/types";

const rootReducer = combineReducers({
    scales: scalesReducer,
    newTrial: newTrialReducer,
    activeTrials: activeTrialsReducer,
});

export interface AppState {
    scales: ScalesState;
    newTrial: NewTrialState;
    activeTrials: ActiveTrialsState;
}

export default function configureStore() {
    const middlewares = [thunkMiddleware];
    const middlewareEnhancer = applyMiddleware(...middlewares);

    return createStore(
        rootReducer,
        composeWithDevTools(middlewareEnhancer),
    );
}
