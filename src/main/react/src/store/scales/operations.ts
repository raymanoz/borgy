import {Action} from "redux";
import {ThunkAction} from "redux-thunk";
import {server} from "../../utils/server";
import {AppState} from "../index";
import {receivedScalesResponse} from "./actions";

export const fetchScales = (): ThunkAction<void, AppState, null, Action<string>> =>
    async (dispatch) =>
        fetch(server.scales)
            .then((result) => result.json())
            .then((s) => dispatch(receivedScalesResponse(s)));
