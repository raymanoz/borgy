import {History} from "history";
import React, {Component} from "react";
import {connect} from "react-redux";
import {NavLink} from "react-router-dom";
import {JsonDecoder} from "ts.data.json";
import {AppState} from "../store";
import {fetchScales} from "../store/scales/operations";
import {server} from "../utils/server";
import "./Home.css";
import NewTrial from "./home/NewTrial";
import {Scales, Trials} from "./Scale";

interface StartTrialResponse {
    url: string;
}

JsonDecoder.object<StartTrialResponse>(
    {
        url: JsonDecoder.string,
    },
    "StartTrialResponse",
);

interface Props {
    history: History;
}

interface State {
    trials: Trials;
    trialName: string;
    trialScales: Scales;
}

class Home extends Component<Props, State> {

    constructor(props: Readonly<Props>) {
        super(props);
        this.state = {
            trials: [],
            trialName: "",
            trialScales: [],
        };
        this.renderActiveTrial = this.renderActiveTrial.bind(this);
        this.completeTrial = this.completeTrial.bind(this);
    }

    public componentDidMount() {
        this.refreshTrials();
    }

    public render() {
        return <div className="container">
            <div className="row col justify-content-center"><h1>Borgy</h1></div>
            <NewTrial history={this.props.history}/>

            <span id="activeTrials">
                <div className="row col"><h2>Active Trials</h2></div>
                {this.state ? this.state.trials.map(this.renderActiveTrial) : <span>Loading active trials</span>}
            </span>
        </div>;
    }

    private refreshTrials() {
        fetch(server.trials)
            .then((result) => result.json())
            .then((t) => this.setState({trials: t}));
    }

    private renderActiveTrial(trial: string, idx: number) {
        return <div key={idx} className="row active-trial">
            <div className="col-4"><NavLink to={"/trial/" + trial}>{trial}</NavLink></div>
            <button className="btn btn-danger" onClick={(_) => this.completeTrial(trial)}>Complete</button>
        </div>;
    }

    private completeTrial(trialName: string) {
        fetch(server.trial(trialName), {method: "DELETE", credentials: "same-origin"})
            .then((_) => this.refreshTrials());
    }

}

const mapStateToProps = (state: AppState) => ({
    scales: state.scales,
});

export default connect(
    mapStateToProps,
    { fetchScales },
)(Home);
