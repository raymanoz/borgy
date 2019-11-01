import React, {Component} from "react";
import {connect} from "react-redux";
import {NavLink} from "react-router-dom";
import {Action} from "redux";
import {AppState} from "../../store";
import {refreshTrials} from "../../store/activetrials/operations";
import {server} from "../../utils/server";
import {Trials} from "../Scale";

interface Props {
    refreshTrials: () => Action;

    trials: Trials;
}

class ActiveTrials extends Component<Props> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.renderActiveTrial = this.renderActiveTrial.bind(this);
        this.completeTrial = this.completeTrial.bind(this);
    }

    public componentDidMount() {
        this.props.refreshTrials();
    }

    public render() {
        return (
            <span id="activeTrials">
                <div className="row col"><h2>Active Trials</h2></div>
                {this.props.trials.map(this.renderActiveTrial)}
            </span>
        );
    }

    private renderActiveTrial(trial: string, idx: number) {
        return <div key={idx} className="row active-trial">
            <div className="col-4"><NavLink to={"/trial/" + trial}>{trial}</NavLink></div>
            <button className="btn btn-danger" onClick={(_) => this.completeTrial(trial)}>Complete</button>
        </div>;
    }

    private completeTrial(trialName: string) {
        fetch(server.trial(trialName), {method: "DELETE", credentials: "same-origin"})
            .then(() => this.props.refreshTrials());
    }
}

const mapStateToProps = (state: AppState) => ({
    trials: state.activeTrials,
});

export default connect(
    mapStateToProps,
    { refreshTrials },
)(ActiveTrials);
