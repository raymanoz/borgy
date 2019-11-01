import {applyMiddleware, combineReducers, createStore} from "redux";
import {composeWithDevTools} from "redux-devtools-extension";
import thunkMiddleware from "redux-thunk";
import {Scales} from "../pages/Scale";
import {newTrialReducer} from "./newtrial/reducer";
import {NewTrialState} from "./newtrial/types";
import {scalesReducer} from "./scales/reducers";

const rootReducer = combineReducers({
    scales: scalesReducer,
    newTrial: newTrialReducer,
});

export interface AppState {
    scales: Scales;
    newTrial: NewTrialState;
}

export default function configureStore() {
    const middlewares = [thunkMiddleware];
    const middlewareEnhancer = applyMiddleware(...middlewares);

    return createStore(
        rootReducer,
        composeWithDevTools(middlewareEnhancer),
    );
}
